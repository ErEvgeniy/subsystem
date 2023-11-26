package ru.ermolaev.services.notification.sms.sender.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.notification.models.model.NotificationResult;
import ru.ermolaev.services.notification.models.constant.NotificationStatus;
import ru.ermolaev.services.notification.models.model.SmsNotification;
import ru.ermolaev.services.notification.sms.sender.service.SmsNotificationService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsNotificationServiceImpl implements SmsNotificationService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendNotification(SmsNotification notification) {
        NotificationResult result = new NotificationResult();
        result.setNotificationId(notification.getId());
        try {
            // Implement sms notification logic
            log.info("Notification sent to: {}; Message: {}",
                    notification.getPhoneNumber(), notification.getMessage());
            result.setStatus(NotificationStatus.SENT);
        } catch (Exception ex) {
            log.error("Error occurred while send sms to: {}", notification.getPhoneNumber());
            result.setStatus(NotificationStatus.ERROR);
            result.setReason("Error description");
        }
        result.setSentDate(LocalDateTime.now());
        rabbitTemplate.convertAndSend("notification-result-exchange",
                "notification.subs.result", result);
    }

}
