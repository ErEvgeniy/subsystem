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
public class ChargeCacheTaskletConfig extends AbstractCacheTaskletConfig {

    public static final String CHARGES_CACHE_NAME = "charges_id_cache";

    public ChargeCacheTaskletConfig(
            JobRepository jobRepository,
            CacheManager cacheManager,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate
    ) {
        super(jobRepository, cacheManager, platformTransactionManager, targetJdbcTemplate);
    }

    @Bean
    public TaskletStep createChargeCacheTasklet() {
        return buildCreateAndLoadCacheTaskletStep();
    }

    @Bean
    public TaskletStep removeChargeCacheTasklet() {
        return buildRemoveCacheTaskletStep();
    }

    @Override
    protected String getCreateAndLoadTaskletStepName() {
        return "createChargeCacheTasklet";
    }

    @Override
    protected String getRemoveCacheTaskletStepName() {
        return "removeChargeCacheTasklet";
    }

    @Override
    protected String getCacheName() {
        return CHARGES_CACHE_NAME;
    }

    @Override
    protected String getEntityCountSqlQuery() {
        return "SELECT COUNT(*) FROM charges";
    }

    @Override
    protected String getSelectEntitiesSqlQuery() {
        return "SELECT charge_id, external_id FROM charges";
    }

    @Override
    protected RowMapper<IdPair> entityRowMapper() {
        return ((rs, rowNum) -> IdPair.builder()
                .id(rs.getLong("charge_id"))
                .externalId(rs.getLong("external_id"))
                .build());
    }

}
