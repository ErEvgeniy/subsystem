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
public class ChargeTargetCacheTaskletConfig extends AbstractCacheTaskletConfig {

    public static final String CHARGE_TARGETS_CACHE_NAME = "charge_targets_id_cache";

    public ChargeTargetCacheTaskletConfig(
            JobRepository jobRepository,
            CacheManager cacheManager,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate
    ) {
        super(jobRepository, cacheManager, platformTransactionManager, targetJdbcTemplate);
    }

    @Bean
    public TaskletStep createChargeTargetCacheTasklet() {
        return buildCreateAndLoadCacheTaskletStep();
    }

    @Bean
    public TaskletStep removeChargeTargetCacheTasklet() {
        return buildRemoveCacheTaskletStep();
    }

    @Override
    protected String getCreateAndLoadTaskletStepName() {
        return "createChargeTargetCacheTasklet";
    }

    @Override
    protected String getRemoveCacheTaskletStepName() {
        return "removeChargeTargetCacheTasklet";
    }

    @Override
    protected String getCacheName() {
        return CHARGE_TARGETS_CACHE_NAME;
    }

    @Override
    protected String getEntityCountSqlQuery() {
        return "SELECT COUNT(*) FROM charge_targets";
    }

    @Override
    protected String getSelectEntitiesSqlQuery() {
        return "SELECT charge_target_id, external_id FROM charge_targets";
    }

    @Override
    protected RowMapper<IdPair> entityRowMapper() {
        return ((rs, rowNum) -> IdPair.builder()
                .id(rs.getLong("charge_target_id"))
                .externalId(rs.getLong("external_id"))
                .build());
    }

}
