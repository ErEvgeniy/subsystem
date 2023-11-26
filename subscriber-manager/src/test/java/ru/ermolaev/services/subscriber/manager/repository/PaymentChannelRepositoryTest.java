package ru.ermolaev.services.subscriber.manager.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.ermolaev.services.subscriber.manager.domain.PaymentChannel;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.assertSessionStatementExecutionCount;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.clearSessionStatistic;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.countExistedEntities;

@DisplayName("Репозиторий для работы с каналами платежей ")
@DataJpaTest
public class PaymentChannelRepositoryTest {

    private static final String UPDATE_SUFFIX = "_upd";

    @Autowired
    private PaymentChannelRepository paymentChannelRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен получить канал платежа по ID за 1 обращение к БД")
    void shouldFindPaymentChannelById() {
        clearSessionStatistic(testEntityManager);

        Optional<PaymentChannel> optionalPaymentChannelFromRepo = paymentChannelRepository.findById(1L);
        PaymentChannel paymentChannelFromTEM = testEntityManager.find(PaymentChannel.class, 1L);

        assertThat(optionalPaymentChannelFromRepo)
                .isNotEmpty()
                .contains(paymentChannelFromTEM);

        PaymentChannel paymentChannelFromRepo = optionalPaymentChannelFromRepo.get();

        assertThat(paymentChannelFromRepo.getId()).isEqualTo(paymentChannelFromTEM.getId());
        assertThat(paymentChannelFromRepo.getExternalId()).isEqualTo(paymentChannelFromTEM.getExternalId());
        assertThat(paymentChannelFromRepo.getName()).isEqualTo(paymentChannelFromTEM.getName());

        assertSessionStatementExecutionCount(testEntityManager, 1);
    }

    @Test
    @DisplayName("должен не найти канал платежа по несуществующему ID")
    void shouldNotFindPaymentChannelById() {
        Optional<PaymentChannel> notExistedPaymentChannel = paymentChannelRepository.findById(999L);
        assertThat(notExistedPaymentChannel).isEmpty();
    }

    @Test
    @DisplayName("должен найти все каналы платежей")
    void shouldFindAllPaymentChannels() {
        List<PaymentChannel> paymentChannels = paymentChannelRepository.findAll();
        assertThat(paymentChannels)
                .isNotEmpty()
                .hasSize(countExistedEntities(testEntityManager, PaymentChannel.class.getName()));
    }

    @Test
    @DisplayName("должен сохранить новый канал платежа")
    void shouldSavePaymentChannel() {
        int beforeSave = countExistedEntities(testEntityManager, PaymentChannel.class.getName());

        PaymentChannel newPaymentChannel = DummyDataHelper.getDummyPaymentChannel();
        assertThat(newPaymentChannel.getId()).isNull();

        paymentChannelRepository.save(newPaymentChannel);
        assertThat(newPaymentChannel.getId()).isNotNull();

        int afterSave = countExistedEntities(testEntityManager, PaymentChannel.class.getName());

        assertThat(beforeSave + 1).isEqualTo(afterSave);
    }

    @Test
    @DisplayName("должен обновить существующий канал платежа")
    void shouldUpdatePaymentChannel() {
        PaymentChannel paymentChannelToUpdate = testEntityManager.find(PaymentChannel.class, 1L);

        Long newExistedId = paymentChannelToUpdate.getExternalId() + 1L;
        String newPaymentChannelName = paymentChannelToUpdate.getName() + UPDATE_SUFFIX;

        paymentChannelToUpdate.setExternalId(newExistedId);
        paymentChannelToUpdate.setName(newPaymentChannelName);

        paymentChannelRepository.save(paymentChannelToUpdate);

        PaymentChannel updatedPaymentChannel = testEntityManager.find(PaymentChannel.class, 1L);

        assertThat(updatedPaymentChannel.getExternalId()).isEqualTo(newExistedId);
        assertThat(updatedPaymentChannel.getName()).isEqualTo(newPaymentChannelName);
    }

    @Test
    @DisplayName("должен выбросить исключение при удалении используемого канала платежа по ID")
    void shouldNotDeleteUsedPaymentChannelById() {
        paymentChannelRepository.deleteById(1L);
        assertThatThrownBy(() -> testEntityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("должен удалить не используемый канал платежа по ID")
    void shouldDeleteUnusedPaymentChannelById() {
        PaymentChannel toDelete = paymentChannelRepository.save(DummyDataHelper.getDummyPaymentChannel());

        int beforeDelete = countExistedEntities(testEntityManager, PaymentChannel.class.getName());

        paymentChannelRepository.deleteById(toDelete.getId());

        int afterDelete = countExistedEntities(testEntityManager, PaymentChannel.class.getName());

        assertThat(beforeDelete - 1).isEqualTo(afterDelete);
    }

}
