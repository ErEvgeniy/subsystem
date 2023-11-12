package ru.ermolaev.services.data.actuator.step;

import lombok.RequiredArgsConstructor;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.ermolaev.services.data.actuator.classifier.MigrationDataClassifier;
import ru.ermolaev.services.data.actuator.model.ChargeTarget;
import ru.ermolaev.services.data.actuator.model.WriteStrategy;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ChargeTargetMigrationStepConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public JdbcCursorItemReader<ChargeTarget> sourceChargeTargetReader(
            @Qualifier("sourceDataSource") DataSource sourceDataSource) {
        return new JdbcCursorItemReaderBuilder<ChargeTarget>()
                .name("sourceChargeTargetReader")
                .dataSource(sourceDataSource)
                .sql("SELECT id, ConstName FROM StringConst_R")
                .rowMapper(sourceChargeTargetRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<ChargeTarget, ChargeTarget> chargeTargetStrategyProcessor(
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate) {
        return chargeTarget -> {
            String sql = "SELECT COUNT(*) FROM charge_targets WHERE external_id = :id";
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("id", chargeTarget.getId());
            Integer existedEntry = targetJdbcTemplate.queryForObject(sql, parameters, Integer.class);
            if (existedEntry != null && existedEntry > 0) {
                chargeTarget.setWriteStrategy(WriteStrategy.UPDATE);
            }
            return chargeTarget;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ChargeTarget> createChargeTargetWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<ChargeTarget>()
                .dataSource(targetDataSource)
                .sql("INSERT INTO charge_targets(charge_target_id, external_id, name) " +
                        "VALUES (nextval('charge_target_id_seq'), :id, :name)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<ChargeTarget> updateChargeTargetWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<ChargeTarget>()
                .dataSource(targetDataSource)
                .sql("UPDATE charge_targets SET name = :name WHERE external_id = :id")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<ChargeTarget> classifierCompositeChargeTargetWriter(
             @Qualifier("createChargeTargetWriter") JdbcBatchItemWriter<ChargeTarget> createChargeTargetWriter,
             @Qualifier("updateChargeTargetWriter") JdbcBatchItemWriter<ChargeTarget> updateChargeTargetWriter
    ) {
        ClassifierCompositeItemWriter<ChargeTarget> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(new MigrationDataClassifier(createChargeTargetWriter, updateChargeTargetWriter));
        return writer;
    }

    @Bean
    public Step chargeTargetMigrationStep(
            JdbcCursorItemReader<ChargeTarget> sourceChargeTargetReader,
            ItemProcessor<ChargeTarget, ChargeTarget> chargeTargetStrategyProcessor,
            ClassifierCompositeItemWriter<ChargeTarget> classifierCompositeItemWriter
    ) {
        return new StepBuilder("chargeTargetMigrationStep", jobRepository)
                .<ChargeTarget, ChargeTarget> chunk(10, platformTransactionManager)
                .reader(sourceChargeTargetReader)
                .processor(chargeTargetStrategyProcessor)
                .writer(classifierCompositeItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    private RowMapper<ChargeTarget> sourceChargeTargetRowMapper() {
        return (rs, rowNum) -> ChargeTarget.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("ConstName"))
                .build();
    }

}
