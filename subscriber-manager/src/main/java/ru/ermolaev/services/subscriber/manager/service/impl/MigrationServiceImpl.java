package ru.ermolaev.services.subscriber.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.subscriber.manager.service.MigrationService;

import java.util.UUID;

import static ru.ermolaev.services.subscriber.manager.configuration.RabbitMqConfiguration.MIGRATION_EXCHANGE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationServiceImpl implements MigrationService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendRequestForMigration() {
        String migrationRequestUUID = UUID.randomUUID().toString();
        // TODO add userId
        log.info("Migration initialized by user: {}. Request uuid: {}", 1, migrationRequestUUID);
        rabbitTemplate.convertAndSend(MIGRATION_EXCHANGE_NAME, "migration", migrationRequestUUID);
    }

}
