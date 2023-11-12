package ru.ermolaev.services.data.actuator.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MigrationJobConfig {

    private final JobRepository jobRepository;

    private final Step cityMigrationStep;

    private final Step streetMigrationStep;

    private final Step chargeTargetMigrationStep;

    private final Step paymentChannelMigrationStep;

    private final Step subscriberMigrationStep;

    private final Step paymentMigrationStep;

    private final Step chargeMigrationStep;

    @Bean
    public Job migrationJob() {
        return new JobBuilder("migrationJob", jobRepository)
                .start(cityMigrationStep)
                .next(streetMigrationStep)
                .next(chargeTargetMigrationStep)
                .next(paymentChannelMigrationStep)
                .next(subscriberMigrationStep)
                .next(paymentMigrationStep)
                .next(chargeMigrationStep)
                .build();
    }

}
