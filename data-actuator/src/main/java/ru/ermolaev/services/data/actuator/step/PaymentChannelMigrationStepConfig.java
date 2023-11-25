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
import ru.ermolaev.services.data.actuator.model.PaymentChannel;

import javax.sql.DataSource;

import static ru.ermolaev.services.data.actuator.tasklet.PaymentChannelCacheTaskletConfig.PAYMENT_CHANNELS_CACHE_NAME;

@Configuration
public class PaymentChannelMigrationStepConfig extends AbstractMigrationStepConfig {

    public PaymentChannelMigrationStepConfig(
            CacheManager cacheManager,
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate
    ) {
        super(cacheManager, jobRepository, platformTransactionManager, targetJdbcTemplate);
    }

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
    public ItemProcessor<PaymentChannel, PaymentChannel> paymentChannelStrategyProcessor() {
        return paymentChannel -> {
            resolveWriteStrategy(paymentChannel);
            return paymentChannel;
        };
    }

    @Override
    protected String getCacheName() {
        return PAYMENT_CHANNELS_CACHE_NAME;
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
    @Transactional
    public Step paymentChannelMigrationStep(
            JdbcCursorItemReader<PaymentChannel> sourcePaymentChannelReader,
            ItemProcessor<PaymentChannel, PaymentChannel> paymentChannelStrategyProcessor,
            ClassifierCompositeItemWriter<PaymentChannel> classifierCompositeItemWriter
    ) {
        return new StepBuilder("paymentChannelMigrationStep", getJobRepository())
                .<PaymentChannel, PaymentChannel> chunk(10, getPlatformTransactionManager())
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
