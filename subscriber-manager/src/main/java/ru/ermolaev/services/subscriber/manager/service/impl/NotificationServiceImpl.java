package ru.ermolaev.services.subscriber.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ermolaev.services.notification.models.model.ClientNotification;
import ru.ermolaev.services.notification.models.model.EmailNotification;
import ru.ermolaev.services.notification.models.model.NotificationResult;
import ru.ermolaev.services.notification.models.constant.NotificationStatus;
import ru.ermolaev.services.notification.models.model.SmsNotification;
import ru.ermolaev.services.subscriber.manager.constant.NotificationChannel;
import ru.ermolaev.services.subscriber.manager.constant.NotificationTemplate;
import ru.ermolaev.services.subscriber.manager.domain.Notification;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.exception.BusinessException;
import ru.ermolaev.services.subscriber.manager.mapper.NotificationMapper;
import ru.ermolaev.services.subscriber.manager.property.LocaleProvider;
import ru.ermolaev.services.subscriber.manager.repository.NotificationRepository;
import ru.ermolaev.services.subscriber.manager.repository.SubscriberRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.NotificationDto;
import ru.ermolaev.services.subscriber.manager.service.NotificationService;

import java.util.List;

import static ru.ermolaev.services.subscriber.manager.configuration.RabbitMqConfiguration.NOTIFICATION_REQUEST_EXCHANGE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final RabbitTemplate rabbitTemplate;

    private final MessageSource messageSource;

    private final LocaleProvider localeProvider;

    private final SubscriberRepository subscriberRepository;

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void notifySubscriber(long subscriberId, String templateName, String channelName) {
        NotificationTemplate template = resolveTemplate(templateName);
        NotificationChannel channel = resolveChannel(channelName);
        Subscriber subscriber = getSubscriber(subscriberId);
        Notification notification = createNotification(template, subscriber, channel);
        String routingKey = resolveRoutingKey(channel);

        ClientNotification clientNotification;

        // TODO change switch logic to strategy
        switch (channel) {
            case SMS -> clientNotification = createSmsNotification(notification);
            case EMAIL -> clientNotification = createEmailNotification(notification);
            default -> throw new BusinessException("Unsupported notification channel");
        }

        log.info("Send request: {} for notify subscriber: {} by {}",
                notification.getId(), subscriberId, channel);
        rabbitTemplate.convertAndSend(NOTIFICATION_REQUEST_EXCHANGE_NAME, routingKey, clientNotification);
    }

    private SmsNotification createSmsNotification(Notification domain) {
        SmsNotification smsNotification = new SmsNotification();
        smsNotification.setId(domain.getId());
        smsNotification.setMessage(domain.getMessage());
        smsNotification.setPhoneNumber(domain.getDestination());
        return smsNotification;
    }

    private EmailNotification createEmailNotification(Notification domain) {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setId(domain.getId());
        emailNotification.setMessage(domain.getMessage());
        emailNotification.setEmail(domain.getDestination());
        return emailNotification;
    }

    private NotificationChannel resolveChannel(String channelName) {
        return NotificationChannel.valueOf(channelName);
    }

    private NotificationTemplate resolveTemplate(String templateName) {
        return NotificationTemplate.valueOf(templateName);
    }

    private Subscriber getSubscriber(long subscriberId) {
        return subscriberRepository.findById(subscriberId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Subscriber with id - %d not found", subscriberId)));
    }

    private Notification createNotification(
            NotificationTemplate template, Subscriber subscriber, NotificationChannel channel
    ) {
        String message = generateNotificationMessage(template, subscriber);
        String destination = resolveDestination(channel, subscriber);

        Notification notification = new Notification();
        notification.setSubscriber(subscriber);
        notification.setStatus(NotificationStatus.PREPARED.name());
        notification.setMessage(message);
        notification.setDestination(destination);
        notification.setChannel(channel.name());
        return notificationRepository.save(notification);
    }

    private String generateNotificationMessage(NotificationTemplate template, Subscriber subscriber) {
        String[] messageParams;
        switch (template) {
            case NEGATIVE_BALANCE -> messageParams = new String[]{
                    subscriber.getFirstname(), subscriber.getBalance().toString()};
            case CONNECTION_INFO -> messageParams = new String[]{
                    subscriber.getConnectionDate().toString()};
            default -> throw new IllegalStateException("Unexpected template: " + template);
        }
        return messageSource.getMessage(template.getCode(), messageParams, localeProvider.getLocale());
    }

    private String resolveDestination(NotificationChannel channel, Subscriber subscriber) {
        switch (channel) {
            case SMS -> {
                return subscriber.getPhoneNumber();
            }
            case EMAIL -> {
                return subscriber.getEmail();
            }
            default -> throw new IllegalStateException("Unexpected channel: " + channel);
        }
    }

    private String resolveRoutingKey(NotificationChannel channel) {
        switch (channel) {
            case SMS -> {
                return "notification.sms";
            }
            case EMAIL -> {
                return "notification.email";
            }
            default -> throw new IllegalStateException("Unexpected channel: " + channel);
        }
    }

    @Override
    @Transactional
    public void acceptNotificationResult(NotificationResult result) {
        Notification notification = notificationRepository.findById(result.getNotificationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Notification with id - %d not found", result.getNotificationId())));

        notification.setSentDate(result.getSentDate());
        notification.setStatus(result.getStatus().name());
        notification.setReason(result.getReason());

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> findAllBySubscriberId(long subscriberId) {
        List<Notification> notifications = notificationRepository.findAllBySubscriberId(subscriberId);
        return notificationMapper.toDtoList(notifications);
    }

}