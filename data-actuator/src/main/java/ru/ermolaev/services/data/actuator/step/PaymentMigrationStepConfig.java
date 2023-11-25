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
import ru.ermolaev.services.data.actuator.model.Payment;

import javax.sql.DataSource;

import static ru.ermolaev.services.data.actuator.tasklet.PaymentCacheTaskletConfig.PAYMENTS_CACHE_NAME;

@Configuration
public class PaymentMigrationStepConfig extends AbstractMigrationStepConfig {

    public PaymentMigrationStepConfig(
            CacheManager cacheManager,
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate
    ) {
        super(cacheManager, jobRepository, platformTransactionManager, targetJdbcTemplate);
    }

    @Bean
    public JdbcCursorItemReader<Payment> sourcePaymentReader(
            @Qualifier("sourceDataSource") DataSource sourceDataSource) {
        return new JdbcCursorItemReaderBuilder<Payment>()
                .name("sourcePaymentReader")
                .dataSource(sourceDataSource)
                .sql("SELECT PaymentID, PaymentDate, Type, AbonentID, PaymentSum, Period, Other FROM Payments")
                .rowMapper(sourcePaymentRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Payment, Payment> paymentStrategyProcessor() {
        return payment -> {
            resolveWriteStrategy(payment);
            return payment;
        };
    }

    @Override
    protected String getCacheName() {
        return PAYMENTS_CACHE_NAME;
    }

    @Bean
    public JdbcBatchItemWriter<Payment> createPaymentWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<Payment>()
                .dataSource(targetDataSource)
                .sql("INSERT INTO payments(payment_id, external_id, payment_date, " +
                        "payment_channel_id, subscriber_id, amount, period, comment) " +
                        "VALUES (nextval('payment_id_seq'), :id, :paymentDate, " +
                        "(SELECT payment_channel_id FROM payment_channels pc " +
                        "WHERE pc.external_id = :paymentChannelId), " +
                        "(SELECT subscriber_id FROM subscribers s WHERE s.external_id = :subscriberId), " +
                        ":amount, :period, :comment)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Payment> updatePaymentWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<Payment>()
                .dataSource(targetDataSource)
                .sql("UPDATE payments SET payment_date = :paymentDate, payment_channel_id = " +
                        "(SELECT payment_channel_id FROM payment_channels pc " +
                        "WHERE pc.external_id = :paymentChannelId), " +
                        "subscriber_id = (SELECT subscriber_id FROM subscribers s " +
                        "WHERE s.external_id = :subscriberId), " +
                        "amount = :amount, period = :period, comment = :comment WHERE external_id = :id")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<Payment> classifierCompositePaymentWriter(
             @Qualifier("createPaymentWriter") JdbcBatchItemWriter<Payment> createPaymentWriter,
             @Qualifier("updatePaymentWriter") JdbcBatchItemWriter<Payment> updatePaymentWriter
    ) {
        ClassifierCompositeItemWriter<Payment> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(new MigrationDataClassifier(createPaymentWriter, updatePaymentWriter));
        return writer;
    }

    @Bean
    @Transactional
    public Step paymentMigrationStep(
            JdbcCursorItemReader<Payment> sourcePaymentReader,
            ItemProcessor<Payment, Payment> paymentStrategyProcessor,
            ClassifierCompositeItemWriter<Payment> classifierCompositeItemWriter
    ) {
        return new StepBuilder("paymentMigrationStep", getJobRepository())
                .<Payment, Payment> chunk(200, getPlatformTransactionManager())
                .reader(sourcePaymentReader)
                .processor(paymentStrategyProcessor)
                .writer(classifierCompositeItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    private RowMapper<Payment> sourcePaymentRowMapper() {
        return (rs, rowNum) -> Payment.builder()
                .id(rs.getLong("PaymentID"))
                .paymentDate(rs.getDate("PaymentDate").toLocalDate())
                .paymentChannelId(rs.getLong("Type"))
                .subscriberId(rs.getLong("AbonentID"))
                .amount(rs.getFloat("PaymentSum"))
                .period(rs.getString("Period"))
                .comment(rs.getString("Other"))
                .build();
    }

}
