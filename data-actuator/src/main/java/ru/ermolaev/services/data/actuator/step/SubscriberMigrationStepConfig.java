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
import ru.ermolaev.services.data.actuator.model.Subscriber;
import ru.ermolaev.services.data.actuator.model.WriteStrategy;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class SubscriberMigrationStepConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public JdbcCursorItemReader<Subscriber> sourceSubscriberReader(
            @Qualifier("sourceDataSource") DataSource sourceDataSource) {
        return new JdbcCursorItemReaderBuilder<Subscriber>()
                .name("sourceSubscriberReader")
                .dataSource(sourceDataSource)
                .sql("SELECT a.AbonentID, a.Fio, a.Connected, a.ContractN, a.DopLic, a.SOTTELEFON, a.email, " +
                        "a.Saldo, a.ConnectDate, adr.CityID,  adr.StreetID,  adr.House,  adr.Flat " +
                        "FROM Abonents a " +
                        "LEFT JOIN Addresses adr ON a.AddressID = adr.AddressID")
                .rowMapper(sourceSubscriberRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Subscriber, Subscriber> subscriberStrategyProcessor(
            @Qualifier("targetJdbcTemplate") NamedParameterJdbcTemplate targetJdbcTemplate) {
        return subscriber -> {
            String[] nameParts = subscriber.getFullName().split(" ");

            switch (nameParts.length) {
                case 1 -> subscriber.setLastname(nameParts[0]);
                case 2 -> {
                    subscriber.setLastname(nameParts[0]);
                    subscriber.setFirstname(nameParts[1]);
                }
                case 3 -> {
                    subscriber.setLastname(nameParts[0]);
                    subscriber.setFirstname(nameParts[1]);
                    subscriber.setPatronymic(nameParts[2]);
                }
            }

            String sql = "SELECT COUNT(*) FROM subscribers WHERE external_id = :id";
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("id", subscriber.getId());
            Integer existedEntry = targetJdbcTemplate.queryForObject(sql, parameters, Integer.class);
            if (existedEntry != null && existedEntry > 0) {
                subscriber.setWriteStrategy(WriteStrategy.UPDATE);
            }
            return subscriber;
        };
    }

    @Bean
    public JdbcBatchItemWriter<Subscriber> createSubscriberWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<Subscriber>()
                .dataSource(targetDataSource)
                .sql("INSERT INTO subscribers(subscriber_id, external_id, firstname, patronymic, lastname, " +
                        "contract_number, account_number, city_id, street_id, house, flat, phone_number, " +
                        "email, balance, is_active, connection_date) " +
                        "VALUES (nextval('city_id_seq'), :id, :firstname, :patronymic, :lastname, :contractNumber, " +
                        ":accountNumber, (SELECT city_id FROM cities ct WHERE ct.external_id = :cityId), " +
                        "(SELECT street_id FROM streets st WHERE st.external_id = :streetId), " +
                        ":house, :flat, :phoneNumber, :email, :balance, :isActive, :connectionDate)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Subscriber> updateSubscriberWriter(
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<Subscriber>()
                .dataSource(targetDataSource)
                .sql("UPDATE subscribers SET firstname = :firstname, patronymic = :patronymic, " +
                        "lastname = :lastname, " +
                        "contract_number = :contractNumber, account_number = :accountNumber, " +
                        "city_id = (SELECT city_id FROM cities ct WHERE ct.external_id = :cityId), " +
                        "street_id = (SELECT street_id FROM streets st WHERE st.external_id = :streetId), " +
                        "house = :house, flat = :flat, phone_number = :phoneNumber, email = :email, " +
                        "balance = :balance, is_active = :isActive, connection_date = :connectionDate " +
                        "WHERE external_id = :id")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<Subscriber> classifierCompositeSubscriberWriter(
             @Qualifier("createSubscriberWriter") JdbcBatchItemWriter<Subscriber> createSubscriberWriter,
             @Qualifier("updateSubscriberWriter") JdbcBatchItemWriter<Subscriber> updateSubscriberWriter
    ) {
        ClassifierCompositeItemWriter<Subscriber> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(new MigrationDataClassifier(createSubscriberWriter, updateSubscriberWriter));
        return writer;
    }

    @Bean
    public Step subscriberMigrationStep(
            JdbcCursorItemReader<Subscriber> sourceSubscriberReader,
            ItemProcessor<Subscriber, Subscriber> subscriberStrategyProcessor,
            ClassifierCompositeItemWriter<Subscriber> classifierCompositeItemWriter
    ) {
        return new StepBuilder("subscriberMigrationStep", jobRepository)
                .<Subscriber, Subscriber> chunk(10, platformTransactionManager)
                .reader(sourceSubscriberReader)
                .processor(subscriberStrategyProcessor)
                .writer(classifierCompositeItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    private RowMapper<Subscriber> sourceSubscriberRowMapper() {
        return (rs, rowNum) -> Subscriber.builder()
                .id(rs.getLong("AbonentID"))
                .fullName(rs.getString("Fio"))
                .isActive(rs.getBoolean("Connected"))
                .contractNumber(rs.getString("ContractN"))
                .accountNumber(rs.getString("DopLic"))
                .phoneNumber(rs.getString("SOTTELEFON"))
                .email(rs.getString("email"))
                .balance(rs.getFloat("Saldo"))
                .connectionDate(rs.getDate("ConnectDate").toLocalDate())
                .cityId(rs.getLong("CityID"))
                .streetId(rs.getLong("StreetID"))
                .house(rs.getInt("House"))
                .flat(rs.getInt("Flat"))
                .build();
    }

}
