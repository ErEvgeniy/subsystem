package ru.ermolaev.services.data.actuator.configuration;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EhcacheConfiguration {

    @Bean
    public CacheManager ehcacheManager() {
        return CacheManagerBuilder
                .newCacheManagerBuilder()
                .build(true);
    }

}
