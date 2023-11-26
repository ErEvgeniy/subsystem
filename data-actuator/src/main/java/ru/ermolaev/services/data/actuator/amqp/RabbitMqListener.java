package ru.ermolaev.services.data.actuator.amqp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.data.actuator.service.MigrationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqListener {

	private final MigrationService migrationService;

	@RabbitListener(queues = "migration-queue")
	public void processMigrationRequest(String message) {
		log.info("Received request: {} from migration-queue", message);
		migrationService.startMigration();
	}

}