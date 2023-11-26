package ru.ermolaev.services.data.actuator.tasklet;

import org.ehcache.CacheManager;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.ermolaev.services.data.actuator.model.IdPair;

@Configuration
public class PaymentChannelCacheTaskletConfig extends AbstractCacheTaskletConfig {

    public static final String PAYMENT_CHANNELS_CACHE_NAME = "payment_channels_id_cache";

    public PaymentChannelCacheTaskletConfig(
            JobRepository jobRepository,
            CacheManager cacheManager,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate
    ) {
        super(jobRepository, cacheManager, platformTransactionManager, targetJdbcTemplate);
    }

    @Bean
    public TaskletStep createPaymentChannelCacheTasklet() {
        return buildCreateAndLoadCacheTaskletStep();
    }

    @Bean
    public TaskletStep removePaymentChannelCacheTasklet() {
        return buildRemoveCacheTaskletStep();
    }

    @Override
    protected String getCreateAndLoadTaskletStepName() {
        return "createPaymentChannelCacheTasklet";
    }

    @Override
    protected String getRemoveCacheTaskletStepName() {
        return "removePaymentChannelCacheTasklet";
    }

    @Override
    protected String getCacheName() {
        return PAYMENT_CHANNELS_CACHE_NAME;
    }

    @Override
    protected String getEntityCountSqlQuery() {
        return "SELECT COUNT(*) FROM payment_channels";
    }

    @Override
    protected String getSelectEntitiesSqlQuery() {
        return "SELECT payment_channel_id, external_id FROM payment_channels";
    }

    @Override
    protected RowMapper<IdPair> entityRowMapper() {
        return ((rs, rowNum) -> IdPair.builder()
                .id(rs.getLong("payment_channel_id"))
                .externalId(rs.getLong("external_id"))
                .build());
    }

}
