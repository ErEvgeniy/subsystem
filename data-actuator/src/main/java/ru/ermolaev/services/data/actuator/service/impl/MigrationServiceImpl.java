package ru.ermolaev.services.data.actuator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;
import ru.ermolaev.services.data.actuator.dao.MigrationDao;
import ru.ermolaev.services.data.actuator.service.MigrationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MigrationServiceImpl implements MigrationService {

    private final Job migrationJob;

    private final JobLauncher jobLauncher;

    private final MigrationDao migrationDao;

    @Override
    public void startMigration() {
        log.info("Start migration");

        if (migrationDao.isMigrationLocked()) {
            log.error("Execute migration not possible. Another migration is running");
            return;
        }

        migrationDao.lockMigration();

        try {
            jobLauncher.run(migrationJob, new JobParameters());
        } catch (Exception ex) {
            log.error("Exception occurred while processing migration. Stop migration", ex);
            migrationDao.unlockMigration();
            return;
        }

        migrationDao.unlockMigration();
        log.info("Migration complete");
    }

}
