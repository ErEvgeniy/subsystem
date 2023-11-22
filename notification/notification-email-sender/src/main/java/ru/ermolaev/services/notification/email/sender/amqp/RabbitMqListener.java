package ru.ermolaev.services.notification.email.sender.amqp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.notification.email.sender.service.EmailNotificationService;
import ru.ermolaev.services.notification.models.model.EmailNotification;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqListener {

	private final EmailNotificationService emailNotificationService;

	@RabbitListener(queues = "email-queue")
	public void processEmailNotification(EmailNotification notification) {
		log.info("Received request from email-queue. Notification id: {}", notification.getId());
		emailNotificationService.sendNotification(notification);
	}

}