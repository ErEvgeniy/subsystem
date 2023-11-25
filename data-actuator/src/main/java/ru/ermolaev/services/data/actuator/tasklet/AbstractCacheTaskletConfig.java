package ru.ermolaev.services.data.actuator.tasklet;

import lombok.RequiredArgsConstructor;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.ermolaev.services.data.actuator.model.IdPair;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractCacheTaskletConfig {

    private final JobRepository jobRepository;

    private final CacheManager cacheManager;

    private final PlatformTransactionManager platformTransactionManager;

    private final NamedParameterJdbcTemplate targetJdbcTemplate;

    protected TaskletStep buildCreateAndLoadCacheTaskletStep() {
        return new StepBuilder(getCreateAndLoadTaskletStepName(), jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    Integer entryCount = targetJdbcTemplate.queryForObject(
                            getEntityCountSqlQuery(), new MapSqlParameterSource(), Integer.class);
                    if (entryCount != null && entryCount > 0) {
                        CacheConfiguration<Long, Long> cacheConfig = CacheConfigurationBuilder
                                .newCacheConfigurationBuilder(
                                        Long.class, Long.class, ResourcePoolsBuilder.heap(entryCount))
                                .build();
                        Cache<Long, Long> cache = cacheManager.createCache(getCacheName(), cacheConfig);

                        List<IdPair> idPairList = targetJdbcTemplate.query(
                                getSelectEntitiesSqlQuery(), new MapSqlParameterSource(), entityRowMapper());

                        idPairList.forEach(pair -> cache.put(pair.getExternalId(), pair.getId()));
                    }
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    protected TaskletStep buildRemoveCacheTaskletStep() {
        return new StepBuilder(getRemoveCacheTaskletStepName(), jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    cacheManager.removeCache(getCacheName());
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    protected abstract String getCreateAndLoadTaskletStepName();

    protected abstract String getRemoveCacheTaskletStepName();

    protected abstract String getCacheName();

    protected abstract String getEntityCountSqlQuery();

    protected abstract String getSelectEntitiesSqlQuery();

    protected abstract RowMapper<IdPair> entityRowMapper();

}
