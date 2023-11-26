package ru.ermolaev.services.data.actuator.step;

import org.ehcache.CacheManager;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import ru.ermolaev.services.data.actuator.classifier.MigrationDataClassifier;
import ru.ermolaev.services.data.actuator.model.City;

import javax.sql.DataSource;

import static ru.ermolaev.services.data.actuator.tasklet.CityCacheTaskletConfig.CITIES_CACHE_NAME;

@Configuration
public class CityMigrationStepConfig extends AbstractMigrationStepConfig {

    public CityMigrationStepConfig(
            CacheManager cacheManager,
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate
    ) {
        super(cacheManager, jobRepository, platformTransactionManager, targetJdbcTemplate);
    }

    @Bean
    public JdbcCursorItemReader<City> sourceCityReader(
            @Qualifier("sourceDataSource") DataSource sourceDataSource) {
        return new JdbcCursorItemReaderBuilder<City>()
                .name("sourceCityReader")
                .dataSource(sourceDataSource)
                .sql("SELECT CityID, CityName FROM Cities")
                .rowMapper(sourceCityRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<City, City> cityStrategyProcessor() {
        return city -> {
            resolveWriteStrategy(city);
            return city;
        };
    }

    @Override
    protected String getCacheName() {
        return CITIES_CACHE_NAME;
    }

    @Bean
    public JdbcBatchItemWriter<City> createCityWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<City>()
                .dataSource(targetDataSource)
                .sql("INSERT INTO cities(city_id, external_id, name) " +
                        "VALUES (nextval('city_id_seq'), :id, :name)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<City> updateCityWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<City>()
                .dataSource(targetDataSource)
                .sql("UPDATE cities SET name = :name WHERE external_id = :id")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<City> classifierCompositeCityWriter(
             @Qualifier("createCityWriter") JdbcBatchItemWriter<City> createCityWriter,
             @Qualifier("updateCityWriter") JdbcBatchItemWriter<City> updateCityWriter
    ) {
        ClassifierCompositeItemWriter<City> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(new MigrationDataClassifier(createCityWriter, updateCityWriter));
        return writer;
    }

    @Bean
    @Transactional
    public Step cityMigrationStep(
            JdbcCursorItemReader<City> sourceCityReader,
            ItemProcessor<City, City> cityStrategyProcessor,
            ClassifierCompositeItemWriter<City> classifierCompositeItemWriter
    ) {
        return new StepBuilder("cityMigrationStep", getJobRepository())
                .<City, City> chunk(10, getPlatformTransactionManager())
                .reader(sourceCityReader)
                .processor(cityStrategyProcessor)
                .writer(classifierCompositeItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    private RowMapper<City> sourceCityRowMapper() {
        return (rs, rowNum) -> City.builder()
                .id(rs.getLong("CityID"))
                .name(rs.getString("CityName"))
                .build();
    }

}
