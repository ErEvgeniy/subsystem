package ru.ermolaev.services.data.actuator.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ermolaev.services.data.actuator.service.MigrationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitMigrationJob {

    private final MigrationService migrationService;

    @Scheduled(cron = "${migration.job.cron:0 0 1 * * *}")
    public void execute() {
        log.info("Start InitMigrationJob");
        migrationService.startMigration();
        log.info("Stop InitMigrationJob");
    }

}
