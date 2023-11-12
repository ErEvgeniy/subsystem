package ru.ermolaev.services.data.actuator.configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MigrationDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.migration-db")
    public DataSourceProperties migrationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource migrationDataSource() {
        return migrationDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

}
