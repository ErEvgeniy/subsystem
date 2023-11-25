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

    private final Step createCityCacheTasklet;

    private final Step cityMigrationStep;

    private final Step removeCityCacheTasklet;

    private final Step createStreetCacheTasklet;

    private final Step streetMigrationStep;

    private final Step removeStreetCacheTasklet;

    private final Step createChargeTargetCacheTasklet;

    private final Step chargeTargetMigrationStep;

    private final Step removeChargeTargetCacheTasklet;

    private final Step createPaymentChannelCacheTasklet;

    private final Step paymentChannelMigrationStep;

    private final Step removePaymentChannelCacheTasklet;

    private final Step createSubscriberCacheTasklet;

    private final Step subscriberMigrationStep;

    private final Step removeSubscriberCacheTasklet;

    private final Step createPaymentCacheTasklet;

    private final Step paymentMigrationStep;

    private final Step removePaymentCacheTasklet;

    private final Step createChargeCacheTasklet;

    private final Step chargeMigrationStep;

    private final Step removeChargeCacheTasklet;

    @Bean
    public Job migrationJob() {
        return new JobBuilder("migrationJob", jobRepository)
                .start(createCityCacheTasklet).next(cityMigrationStep).next(removeCityCacheTasklet)
                .next(createStreetCacheTasklet).next(streetMigrationStep).next(removeStreetCacheTasklet)
                .next(createChargeTargetCacheTasklet).next(chargeTargetMigrationStep)
                    .next(removeChargeTargetCacheTasklet)
                .next(createPaymentChannelCacheTasklet).next(paymentChannelMigrationStep)
                    .next(removePaymentChannelCacheTasklet)
                .next(createSubscriberCacheTasklet).next(subscriberMigrationStep).next(removeSubscriberCacheTasklet)
                .next(createPaymentCacheTasklet).next(paymentMigrationStep).next(removePaymentCacheTasklet)
                .next(createChargeCacheTasklet).next(chargeMigrationStep).next(removeChargeCacheTasklet)
                .build();
    }

}
