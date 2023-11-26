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
public class PaymentCacheTaskletConfig extends AbstractCacheTaskletConfig {

    public static final String PAYMENTS_CACHE_NAME = "payments_id_cache";

    public PaymentCacheTaskletConfig(
            JobRepository jobRepository,
            CacheManager cacheManager,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate
    ) {
        super(jobRepository, cacheManager, platformTransactionManager, targetJdbcTemplate);
    }

    @Bean
    public TaskletStep createPaymentCacheTasklet() {
        return buildCreateAndLoadCacheTaskletStep();
    }

    @Bean
    public TaskletStep removePaymentCacheTasklet() {
        return buildRemoveCacheTaskletStep();
    }

    @Override
    protected String getCreateAndLoadTaskletStepName() {
        return "createPaymentCacheTasklet";
    }

    @Override
    protected String getRemoveCacheTaskletStepName() {
        return "removePaymentCacheTasklet";
    }

    @Override
    protected String getCacheName() {
        return PAYMENTS_CACHE_NAME;
    }

    @Override
    protected String getEntityCountSqlQuery() {
        return "SELECT COUNT(*) FROM payments";
    }

    @Override
    protected String getSelectEntitiesSqlQuery() {
        return "SELECT payment_id, external_id FROM payments";
    }

    @Override
    protected RowMapper<IdPair> entityRowMapper() {
        return ((rs, rowNum) -> IdPair.builder()
                .id(rs.getLong("payment_id"))
                .externalId(rs.getLong("external_id"))
                .build());
    }

}
