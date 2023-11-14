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
import ru.ermolaev.services.data.actuator.model.Street;

import javax.sql.DataSource;

import static ru.ermolaev.services.data.actuator.tasklet.StreetCacheTaskletConfig.STREETS_CACHE_NAME;

@Configuration
public class StreetMigrationStepConfig extends AbstractMigrationStepConfig {

    public StreetMigrationStepConfig(
            CacheManager cacheManager,
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate
    ) {
        super(cacheManager, jobRepository, platformTransactionManager, targetJdbcTemplate);
    }

    @Bean
    public JdbcCursorItemReader<Street> sourceStreetReader(
            @Qualifier("sourceDataSource") DataSource sourceDataSource) {
        return new JdbcCursorItemReaderBuilder<Street>()
                .name("sourceStreetReader")
                .dataSource(sourceDataSource)
                .sql("SELECT StreetID, StreetName FROM Streets")
                .rowMapper(sourceStreetRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Street, Street> streetStrategyProcessor() {
        return street -> {
            resolveWriteStrategy(street);
            return street;
        };
    }

    @Override
    protected String getCacheName() {
        return STREETS_CACHE_NAME;
    }

    @Bean
    public JdbcBatchItemWriter<Street> createStreetWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<Street>()
                .dataSource(targetDataSource)
                .sql("INSERT INTO streets(street_id, external_id, name) " +
                        "VALUES (nextval('street_id_seq'), :id, :name)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Street> updateStreetWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<Street>()
                .dataSource(targetDataSource)
                .sql("UPDATE streets SET name = :name WHERE external_id = :id")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<Street> classifierCompositeStreetWriter(
             @Qualifier("createStreetWriter") JdbcBatchItemWriter<Street> createStreetWriter,
             @Qualifier("updateStreetWriter") JdbcBatchItemWriter<Street> updateStreetWriter
    ) {
        ClassifierCompositeItemWriter<Street> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(new MigrationDataClassifier(createStreetWriter, updateStreetWriter));
        return writer;
    }

    @Bean
    @Transactional
    public Step streetMigrationStep(
            JdbcCursorItemReader<Street> sourceStreetReader,
            ItemProcessor<Street, Street> streetStrategyProcessor,
            ClassifierCompositeItemWriter<Street> classifierCompositeItemWriter
    ) {
        return new StepBuilder("streetMigrationStep", jobRepository)
                .<Street, Street> chunk(10, platformTransactionManager)
                .reader(sourceStreetReader)
                .processor(streetStrategyProcessor)
                .writer(classifierCompositeItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    private RowMapper<Street> sourceStreetRowMapper() {
        return (rs, rowNum) -> Street.builder()
                .id(rs.getLong("StreetID"))
                .name(rs.getString("StreetName"))
                .build();
    }

}
