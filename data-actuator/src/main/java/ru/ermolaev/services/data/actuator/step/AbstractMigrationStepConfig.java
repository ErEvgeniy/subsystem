package ru.ermolaev.services.data.actuator.step;

import lombok.RequiredArgsConstructor;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.ermolaev.services.data.actuator.model.AbstractMigrationModel;
import ru.ermolaev.services.data.actuator.model.WriteStrategy;

@RequiredArgsConstructor
public abstract class AbstractMigrationStepConfig {

    private final CacheManager cacheManager;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final NamedParameterJdbcTemplate targetJdbcTemplate;

    protected void resolveWriteStrategy(AbstractMigrationModel model) {
        Cache<Long, Long> cache = cacheManager.getCache(getCacheName(), Long.class, Long.class);
        if (cache != null && cache.containsKey(model.getId())) {
            model.setWriteStrategy(WriteStrategy.UPDATE);
        } else {
            model.setWriteStrategy(WriteStrategy.CREATE);
        }
    }

    protected abstract String getCacheName();

    protected CacheManager getCacheManager() {
        return cacheManager;
    }

    protected JobRepository getJobRepository() {
        return jobRepository;
    }

    protected PlatformTransactionManager getPlatformTransactionManager() {
        return platformTransactionManager;
    }

    protected NamedParameterJdbcTemplate getTargetJdbcTemplate() {
        return targetJdbcTemplate;
    }

}
