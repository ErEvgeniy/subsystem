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
import ru.ermolaev.services.data.actuator.model.PaymentChannel;
import ru.ermolaev.services.data.actuator.model.WriteStrategy;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class PaymentChannelMigrationStepConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public JdbcCursorItemReader<PaymentChannel> sourcePaymentChannelReader(
            @Qualifier("sourceDataSource") DataSource sourceDataSource) {
        return new JdbcCursorItemReaderBuilder<PaymentChannel>()
                .name("sourcePaymentChannelReader")
                .dataSource(sourceDataSource)
                .sql("SELECT id, ConstName FROM StringConst")
                .rowMapper(sourcePaymentChannelRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<PaymentChannel, PaymentChannel> paymentChannelStrategyProcessor(
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate) {
        return paymentChannel -> {
            String sql = "SELECT COUNT(*) FROM payment_channels WHERE external_id = :id";
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("id", paymentChannel.getId());
            Integer existedEntry = targetJdbcTemplate.queryForObject(sql, parameters, Integer.class);
            if (existedEntry != null && existedEntry > 0) {
                paymentChannel.setWriteStrategy(WriteStrategy.UPDATE);
            }
            return paymentChannel;
        };
    }

    @Bean
    public JdbcBatchItemWriter<PaymentChannel> createPaymentChannelWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<PaymentChannel>()
                .dataSource(targetDataSource)
                .sql("INSERT INTO payment_channels(payment_channel_id, external_id, name) " +
                        "VALUES (nextval('payment_channel_id_seq'), :id, :name)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<PaymentChannel> updatePaymentChannelWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<PaymentChannel>()
                .dataSource(targetDataSource)
                .sql("UPDATE payment_channels SET name = :name WHERE external_id = :id")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<PaymentChannel> classifierCompositePaymentChannelWriter(
             @Qualifier("createPaymentChannelWriter") JdbcBatchItemWriter<PaymentChannel> createPaymentChannelWriter,
             @Qualifier("updatePaymentChannelWriter") JdbcBatchItemWriter<PaymentChannel> updatePaymentChannelWriter
    ) {
        ClassifierCompositeItemWriter<PaymentChannel> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(new MigrationDataClassifier(createPaymentChannelWriter, updatePaymentChannelWriter));
        return writer;
    }

    @Bean
    public Step paymentChannelMigrationStep(
            JdbcCursorItemReader<PaymentChannel> sourcePaymentChannelReader,
            ItemProcessor<PaymentChannel, PaymentChannel> paymentChannelStrategyProcessor,
            ClassifierCompositeItemWriter<PaymentChannel> classifierCompositeItemWriter
    ) {
        return new StepBuilder("paymentChannelMigrationStep", jobRepository)
                .<PaymentChannel, PaymentChannel> chunk(10, platformTransactionManager)
                .reader(sourcePaymentChannelReader)
                .processor(paymentChannelStrategyProcessor)
                .writer(classifierCompositeItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    private RowMapper<PaymentChannel> sourcePaymentChannelRowMapper() {
        return (rs, rowNum) -> PaymentChannel.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("ConstName"))
                .build();
    }

}
