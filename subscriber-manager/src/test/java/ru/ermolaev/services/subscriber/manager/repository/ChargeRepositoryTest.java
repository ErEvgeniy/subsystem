package ru.ermolaev.services.subscriber.manager.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.ermolaev.services.subscriber.manager.domain.Charge;
import ru.ermolaev.services.subscriber.manager.domain.ChargeTarget;
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

@DisplayName("Репозиторий для работы с начислениями ")
@DataJpaTest
public class ChargeRepositoryTest {

    private static final String UPDATE_SUFFIX = "_upd";

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен получить начисление по ID за 1 обращение к БД")
    void shouldFindChargeById() {
        clearSessionStatistic(testEntityManager);

        Optional<Charge> optionalChargeFromRepo = chargeRepository.findById(1L);
        Charge chargeFromTEM = testEntityManager.find(Charge.class, 1L);

        assertThat(optionalChargeFromRepo)
                .isNotEmpty()
                .contains(chargeFromTEM);

        Charge chargeFromRepo = optionalChargeFromRepo.get();

        assertThat(chargeFromRepo.getId()).isEqualTo(chargeFromTEM.getId());
        assertThat(chargeFromRepo.getExternalId()).isEqualTo(chargeFromTEM.getExternalId());
        assertThat(chargeFromRepo.getChargeDate()).isEqualTo(chargeFromTEM.getChargeDate());
        assertThat(chargeFromRepo.getAmount()).isEqualTo(chargeFromTEM.getAmount());
        assertThat(chargeFromRepo.getPeriod()).isEqualTo(chargeFromTEM.getPeriod());
        assertThat(chargeFromRepo.getComment()).isEqualTo(chargeFromTEM.getComment());

        assertThat(chargeFromRepo.getChargeTarget()).isNotNull();
        assertThat(chargeFromTEM.getChargeTarget()).isNotNull();
        assertThat(chargeFromRepo.getChargeTarget()).isEqualTo(chargeFromTEM.getChargeTarget());
        assertThat(chargeFromRepo.getChargeTarget().getId()).isEqualTo(chargeFromTEM.getChargeTarget().getId());

        assertThat(chargeFromRepo.getSubscriber()).isNotNull();
        assertThat(chargeFromTEM.getSubscriber()).isNotNull();
        assertThat(chargeFromRepo.getSubscriber()).isEqualTo(chargeFromTEM.getSubscriber());
        assertThat(chargeFromRepo.getSubscriber().getFirstname()).isEqualTo(chargeFromTEM.getSubscriber().getFirstname());

        assertSessionStatementExecutionCount(testEntityManager, 1);
    }

    @Test
    @DisplayName("должен не найти начисление по несуществующему ID")
    void shouldNotFindChargeById() {
        Optional<Charge> notExistedCharge = chargeRepository.findById(999L);
        assertThat(notExistedCharge).isEmpty();
    }

    @Test
    @DisplayName("должен найти все начисления")
    void shouldFindAllCharges() {
        List<Charge> charges = chargeRepository.findAll();
        assertThat(charges)
                .isNotEmpty()
                .hasSize(countExistedEntities(testEntityManager, Charge.class.getName()));
    }

    @Test
    @DisplayName("должен найти все начисления конкретного абонента за 1 обращение к БД")
    void shouldFindAllSubscriberCharges() {
        clearSessionStatistic(testEntityManager);

        List<Charge> charges = chargeRepository.findAllBySubscriberId(1L);

        charges.forEach(charge -> assertThat(charge.getSubscriber().getFirstname()).isNotNull());

        assertSessionStatementExecutionCount(testEntityManager, 1);

        assertThat(charges)
                .isNotEmpty()
                .hasSize(countExistedEntitiesBySubscriber(testEntityManager, Charge.class.getName(), 1));
    }

    @Test
    @DisplayName("должен сохранить новое начисление")
    void shouldSaveCharge() {
        int beforeSave = countExistedEntities(testEntityManager, Charge.class.getName());

        ChargeTarget chargeTarget = testEntityManager.find(ChargeTarget.class, 1L);
        Subscriber subscriber = testEntityManager.find(Subscriber.class, 1L);

        Charge newCharge = DummyDataHelper.getDummyCharge();
        newCharge.setChargeTarget(chargeTarget);
        newCharge.setSubscriber(subscriber);

        assertThat(newCharge.getId()).isNull();

        chargeRepository.save(newCharge);
        assertThat(newCharge.getId()).isNotNull();

        int afterSave = countExistedEntities(testEntityManager, Charge.class.getName());

        assertThat(beforeSave + 1).isEqualTo(afterSave);
    }

    @Test
    @DisplayName("должен обновить существующее начисление")
    void shouldUpdateCharge() {
        Charge chargeToUpdate = testEntityManager.find(Charge.class, 1L);

        Long newExistedId = chargeToUpdate.getExternalId() + 1L;
        LocalDate newChargeDate = chargeToUpdate.getChargeDate().plusDays(1L);
        Float newAmount = chargeToUpdate.getAmount() + 1.0F;
        String newPeriod = chargeToUpdate.getPeriod() + UPDATE_SUFFIX;
        String newComment = chargeToUpdate.getComment() + UPDATE_SUFFIX;

        chargeToUpdate.setExternalId(newExistedId);
        chargeToUpdate.setChargeDate(newChargeDate);
        chargeToUpdate.setAmount(newAmount);
        chargeToUpdate.setPeriod(newPeriod);
        chargeToUpdate.setComment(newComment);

        chargeRepository.save(chargeToUpdate);

        Charge updatedCharge = testEntityManager.find(Charge.class, 1L);

        assertThat(updatedCharge.getExternalId()).isEqualTo(newExistedId);
        assertThat(updatedCharge.getChargeDate()).isEqualTo(newChargeDate);
        assertThat(updatedCharge.getAmount()).isEqualTo(newAmount);
        assertThat(updatedCharge.getPeriod()).isEqualTo(newPeriod);
        assertThat(updatedCharge.getComment()).isEqualTo(newComment);
    }

    @Test
    @DisplayName("должен удалить существующее начисление по ID")
    void shouldDeleteChargeById() {
        int beforeDelete = countExistedEntities(testEntityManager, Charge.class.getName());

        chargeRepository.deleteById(1L);

        int afterDelete = countExistedEntities(testEntityManager, Charge.class.getName());

        assertThat(beforeDelete - 1).isEqualTo(afterDelete);
    }

}
