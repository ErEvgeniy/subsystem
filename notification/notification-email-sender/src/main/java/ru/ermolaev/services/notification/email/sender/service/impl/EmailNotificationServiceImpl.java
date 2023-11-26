package ru.ermolaev.services.notification.email.sender.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.notification.email.sender.service.EmailNotificationService;
import ru.ermolaev.services.notification.models.model.EmailNotification;
import ru.ermolaev.services.notification.models.model.NotificationResult;
import ru.ermolaev.services.notification.models.constant.NotificationStatus;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendNotification(EmailNotification notification) {
        NotificationResult result = new NotificationResult();
        result.setNotificationId(notification.getId());
        try {
            // Implement email notification logic
            log.info("Notification sent to: {}; Message: {}",
                    notification.getEmail(), notification.getMessage());
            result.setStatus(NotificationStatus.SENT);
        } catch (Exception ex) {
            log.error("Error occurred while send email to: {}", notification.getEmail());
            result.setStatus(NotificationStatus.ERROR);
            result.setReason("Error description");
        }
        result.setSentDate(LocalDateTime.now());
        rabbitTemplate.convertAndSend("notification-result-exchange",
                "notification.subs.result", result);
    }

}
