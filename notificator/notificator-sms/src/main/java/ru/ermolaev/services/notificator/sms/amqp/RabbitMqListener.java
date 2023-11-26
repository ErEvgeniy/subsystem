package ru.ermolaev.services.notificator.sms.amqp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.notificator.model.ClientNotification;
import ru.ermolaev.services.notificator.sms.service.SmsNotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqListener {

	private final SmsNotificationService smsNotificationService;

	@RabbitListener(queues = "sms-queue")
	public void processSmsNotification(ClientNotification notification) {
		log.info("Received request from sms-queue. Notification id: {}", notification.getId());
		smsNotificationService.sendNotification(notification);
	}

}