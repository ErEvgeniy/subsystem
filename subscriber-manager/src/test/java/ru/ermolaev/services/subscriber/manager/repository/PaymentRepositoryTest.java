package ru.ermolaev.services.subscriber.manager.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.ermolaev.services.subscriber.manager.domain.Payment;
import ru.ermolaev.services.subscriber.manager.domain.PaymentChannel;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.assertSessionStatementExecutionCount;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.clearSessionStatistic;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.countExistedEntities;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.countExistedEntitiesBySubscriber;

@DisplayName("Репозиторий для работы с платежами ")
@DataJpaTest
public class PaymentRepositoryTest {

    private static final String UPDATE_SUFFIX = "_upd";

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен получить платеж по ID за 1 обращение к БД")
    void shouldFindPaymentById() {
        clearSessionStatistic(testEntityManager);

        Optional<Payment> optionalPaymentFromRepo = paymentRepository.findById(1L);
        Payment paymentFromTEM = testEntityManager.find(Payment.class, 1L);

        assertThat(optionalPaymentFromRepo)
                .isNotEmpty()
                .contains(paymentFromTEM);

        Payment paymentFromRepo = optionalPaymentFromRepo.get();

        assertThat(paymentFromRepo.getId()).isEqualTo(paymentFromTEM.getId());
        assertThat(paymentFromRepo.getExternalId()).isEqualTo(paymentFromTEM.getExternalId());
        assertThat(paymentFromRepo.getPaymentDate()).isEqualTo(paymentFromTEM.getPaymentDate());
        assertThat(paymentFromRepo.getAmount()).isEqualTo(paymentFromTEM.getAmount());
        assertThat(paymentFromRepo.getPeriod()).isEqualTo(paymentFromTEM.getPeriod());
        assertThat(paymentFromRepo.getComment()).isEqualTo(paymentFromTEM.getComment());

        assertThat(paymentFromRepo.getPaymentChannel()).isNotNull();
        assertThat(paymentFromTEM.getPaymentChannel()).isNotNull();
        assertThat(paymentFromRepo.getPaymentChannel()).isEqualTo(paymentFromTEM.getPaymentChannel());
        assertThat(paymentFromRepo.getPaymentChannel().getId()).isEqualTo(paymentFromTEM.getPaymentChannel().getId());

        assertThat(paymentFromRepo.getSubscriber()).isNotNull();
        assertThat(paymentFromTEM.getSubscriber()).isNotNull();
        assertThat(paymentFromRepo.getSubscriber()).isEqualTo(paymentFromTEM.getSubscriber());
        assertThat(paymentFromRepo.getSubscriber().getFirstname()).isEqualTo(paymentFromTEM.getSubscriber().getFirstname());

        assertSessionStatementExecutionCount(testEntityManager, 1);
    }

    @Test
    @DisplayName("должен не найти платеж по несуществующему ID")
    void shouldNotFindPaymentById() {
        Optional<Payment> notExistedPayment = paymentRepository.findById(999L);
        assertThat(notExistedPayment).isEmpty();
    }

    @Test
    @DisplayName("должен найти все платежи")
    void shouldFindAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments)
                .isNotEmpty()
                .hasSize(countExistedEntities(testEntityManager, Payment.class.getName()));
    }

    @Test
    @DisplayName("должен найти все платежи конкретного абонента за 1 обращение к БД")
    void shouldFindAllSubscriberPayments() {
        clearSessionStatistic(testEntityManager);

        List<Payment> payments = paymentRepository.findAllBySubscriberId(1L);

        payments.forEach(payment -> assertThat(payment.getSubscriber().getFirstname()).isNotNull());

        assertSessionStatementExecutionCount(testEntityManager, 1);

        assertThat(payments)
                .isNotEmpty()
                .hasSize(countExistedEntitiesBySubscriber(testEntityManager, Payment.class.getName(), 1));
    }

    @Test
    @DisplayName("должен сохранить новый платеж")
    void shouldSavePayment() {
        int beforeSave = countExistedEntities(testEntityManager, Payment.class.getName());

        PaymentChannel paymentChannel = testEntityManager.find(PaymentChannel.class, 1L);
        Subscriber subscriber = testEntityManager.find(Subscriber.class, 1L);

        Payment newPayment = DummyDataHelper.getDummyPayment();
        newPayment.setPaymentChannel(paymentChannel);
        newPayment.setSubscriber(subscriber);

        assertThat(newPayment.getId()).isNull();

        paymentRepository.save(newPayment);
        assertThat(newPayment.getId()).isNotNull();

        int afterSave = countExistedEntities(testEntityManager, Payment.class.getName());

        assertThat(beforeSave + 1).isEqualTo(afterSave);
    }

    @Test
    @DisplayName("должен обновить существующий платеж")
    void shouldUpdatePayment() {
        Payment paymentToUpdate = testEntityManager.find(Payment.class, 1L);

        Long newExistedId = paymentToUpdate.getExternalId() + 1L;
        LocalDate newPaymentDate = paymentToUpdate.getPaymentDate().plusDays(1L);
        Float newAmount = paymentToUpdate.getAmount() + 1.0F;
        String newPeriod = paymentToUpdate.getPeriod() + UPDATE_SUFFIX;
        String newComment = paymentToUpdate.getComment() + UPDATE_SUFFIX;

        paymentToUpdate.setExternalId(newExistedId);
        paymentToUpdate.setPaymentDate(newPaymentDate);
        paymentToUpdate.setAmount(newAmount);
        paymentToUpdate.setPeriod(newPeriod);
        paymentToUpdate.setComment(newComment);

        paymentRepository.save(paymentToUpdate);

        Payment updatedPayment = testEntityManager.find(Payment.class, 1L);

        assertThat(updatedPayment.getExternalId()).isEqualTo(newExistedId);
        assertThat(updatedPayment.getPaymentDate()).isEqualTo(newPaymentDate);
        assertThat(updatedPayment.getAmount()).isEqualTo(newAmount);
        assertThat(updatedPayment.getPeriod()).isEqualTo(newPeriod);
        assertThat(updatedPayment.getComment()).isEqualTo(newComment);
    }

    @Test
    @DisplayName("должен удалить существующей платеж по ID")
    void shouldDeletePaymentById() {
        int beforeDelete = countExistedEntities(testEntityManager, Payment.class.getName());

        paymentRepository.deleteById(1L);

        int afterDelete = countExistedEntities(testEntityManager, Payment.class.getName());

        assertThat(beforeDelete - 1).isEqualTo(afterDelete);
    }

}
