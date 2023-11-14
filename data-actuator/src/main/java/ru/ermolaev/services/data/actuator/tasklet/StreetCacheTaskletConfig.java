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
public class StreetCacheTaskletConfig extends AbstractCacheTaskletConfig {

    public static final String STREETS_CACHE_NAME = "streets_id_cache";

    public StreetCacheTaskletConfig(
            JobRepository jobRepository,
            CacheManager cacheManager,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate
    ) {
        super(jobRepository, cacheManager, platformTransactionManager, targetJdbcTemplate);
    }

    @Bean
    public TaskletStep createStreetCacheTasklet() {
        return buildCreateAndLoadCacheTaskletStep();
    }

    @Bean
    public TaskletStep removeStreetCacheTasklet() {
        return buildRemoveCacheTaskletStep();
    }

    @Override
    protected String getCreateAndLoadTaskletStepName() {
        return "createStreetCacheTasklet";
    }

    @Override
    protected String getRemoveCacheTaskletStepName() {
        return "removeStreetCacheTasklet";
    }

    @Override
    protected String getCacheName() {
        return STREETS_CACHE_NAME;
    }

    @Override
    protected String getEntityCountSqlQuery() {
        return "SELECT COUNT(*) FROM streets";
    }

    @Override
    protected String getSelectEntitiesSqlQuery() {
        return "SELECT street_id, external_id FROM streets";
    }

    @Override
    protected RowMapper<IdPair> entityRowMapper() {
        return ((rs, rowNum) -> IdPair.builder()
                .id(rs.getLong("street_id"))
                .externalId(rs.getLong("external_id"))
                .build());
    }

}
