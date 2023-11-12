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
import ru.ermolaev.services.data.actuator.model.Charge;
import ru.ermolaev.services.data.actuator.model.WriteStrategy;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ChargeMigrationStepConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public JdbcCursorItemReader<Charge> sourceChargeReader(
            @Qualifier("sourceDataSource") DataSource sourceDataSource) {
        return new JdbcCursorItemReaderBuilder<Charge>()
                .name("sourceChargeReader")
                .dataSource(sourceDataSource)
                .sql("SELECT DeptorsID, Data, AddSumID, AbonentID, Summa, Period, PS FROM Debtors")
                .rowMapper(sourceChargeRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Charge, Charge> chargeStrategyProcessor(
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate) {
        return charge -> {
            String sql = "SELECT COUNT(*) FROM charges WHERE external_id = :id";
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("id", charge.getId());
            Integer existedEntry = targetJdbcTemplate.queryForObject(sql, parameters, Integer.class);
            if (existedEntry != null && existedEntry > 0) {
                charge.setWriteStrategy(WriteStrategy.UPDATE);
            }
            return charge;
        };
    }

    @Bean
    public JdbcBatchItemWriter<Charge> createChargeWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<Charge>()
                .dataSource(targetDataSource)
                .sql("INSERT INTO charges(charge_id, external_id, charge_date, " +
                        "charge_target_id, subscriber_id, amount, period, comment) " +
                        "VALUES (nextval('charge_id_seq'), :id, :chargeDate, " +
                        "(SELECT charge_target_id FROM charge_targets ct WHERE ct.external_id = :chargeTargetId), " +
                        "(SELECT subscriber_id FROM subscribers s WHERE s.external_id = :subscriberId), " +
                        ":amount, :period, :comment)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Charge> updateChargeWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<Charge>()
                .dataSource(targetDataSource)
                .sql("UPDATE charges SET charge_date = :chargeDate, charge_target_id = " +
                        "(SELECT charge_target_id FROM charge_targets ct WHERE ct.external_id = :chargeTargetId), " +
                        "subscriber_id = " +
                        "(SELECT subscriber_id FROM subscribers s WHERE s.external_id = :subscriberId), " +
                        "amount = :amount, period = :period, comment = :comment WHERE external_id = :id")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<Charge> classifierCompositeChargeWriter(
             @Qualifier("createChargeWriter") JdbcBatchItemWriter<Charge> createChargeWriter,
             @Qualifier("updateChargeWriter") JdbcBatchItemWriter<Charge> updateChargeWriter
    ) {
        ClassifierCompositeItemWriter<Charge> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(new MigrationDataClassifier(createChargeWriter, updateChargeWriter));
        return writer;
    }

    @Bean
    public Step chargeMigrationStep(
            JdbcCursorItemReader<Charge> sourceChargeReader,
            ItemProcessor<Charge, Charge> chargeStrategyProcessor,
            ClassifierCompositeItemWriter<Charge> classifierCompositeItemWriter
    ) {
        return new StepBuilder("chargeMigrationStep", jobRepository)
                .<Charge, Charge> chunk(10, platformTransactionManager)
                .reader(sourceChargeReader)
                .processor(chargeStrategyProcessor)
                .writer(classifierCompositeItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    private RowMapper<Charge> sourceChargeRowMapper() {
        return (rs, rowNum) -> Charge.builder()
                .id(rs.getLong("DeptorsID"))
                .chargeDate(rs.getDate("Data").toLocalDate())
                .chargeTargetId(rs.getLong("AddSumID"))
                .subscriberId(rs.getLong("AbonentID"))
                .amount(rs.getFloat("Summa"))
                .period(rs.getString("Period"))
                .comment(rs.getString("PS"))
                .build();
    }

}
