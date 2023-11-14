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
public class CityCacheTaskletConfig extends AbstractCacheTaskletConfig {

    public static final String CITIES_CACHE_NAME = "cities_id_cache";

    public CityCacheTaskletConfig(
            JobRepository jobRepository,
            CacheManager cacheManager,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate
    ) {
        super(jobRepository, cacheManager, platformTransactionManager, targetJdbcTemplate);
    }

    @Bean
    public TaskletStep createCityCacheTasklet() {
        return buildCreateAndLoadCacheTaskletStep();
    }

    @Bean
    public TaskletStep removeCityCacheTasklet() {
        return buildRemoveCacheTaskletStep();
    }

    @Override
    protected String getCreateAndLoadTaskletStepName() {
        return "createCityCacheTasklet";
    }

    @Override
    protected String getRemoveCacheTaskletStepName() {
        return "removeCityCacheTasklet";
    }

    @Override
    protected String getCacheName() {
        return CITIES_CACHE_NAME;
    }

    @Override
    protected String getEntityCountSqlQuery() {
        return "SELECT COUNT(*) FROM cities";
    }

    @Override
    protected String getSelectEntitiesSqlQuery() {
        return "SELECT city_id, external_id FROM cities";
    }

    @Override
    protected RowMapper<IdPair> entityRowMapper() {
        return ((rs, rowNum) -> IdPair.builder()
                .id(rs.getLong("city_id"))
                .externalId(rs.getLong("external_id"))
                .build());
    }

}
