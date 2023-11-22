package ru.ermolaev.services.subscriber.manager.integration.amqp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.notification.models.model.NotificationResult;
import ru.ermolaev.services.subscriber.manager.service.NotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqListener {

	private final NotificationService notificationService;

	@RabbitListener(queues = "notification-subs-result-queue")
	public void processNotificationResult(NotificationResult notificationResult) {
		notificationService.acceptNotificationResult(notificationResult);
	}

}