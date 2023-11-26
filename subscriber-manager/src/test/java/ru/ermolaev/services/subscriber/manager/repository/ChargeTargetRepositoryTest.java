package ru.ermolaev.services.subscriber.manager.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.ermolaev.services.subscriber.manager.domain.ChargeTarget;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.assertSessionStatementExecutionCount;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.clearSessionStatistic;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.countExistedEntities;

@DisplayName("Репозиторий для работы с целями списаний ")
@DataJpaTest
public class ChargeTargetRepositoryTest {

    private static final String UPDATE_SUFFIX = "_upd";

    @Autowired
    private ChargeTargetRepository chargeTargetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен получить цель списания по ID за 1 обращение к БД")
    void shouldFindChargeTargetById() {
        clearSessionStatistic(testEntityManager);

        Optional<ChargeTarget> optionalChargeTargetFromRepo = chargeTargetRepository.findById(1L);
        ChargeTarget chargeTargetFromTEM = testEntityManager.find(ChargeTarget.class, 1L);

        assertThat(optionalChargeTargetFromRepo)
                .isNotEmpty()
                .contains(chargeTargetFromTEM);

        ChargeTarget chargeTargetFromRepo = optionalChargeTargetFromRepo.get();

        assertThat(chargeTargetFromRepo.getId()).isEqualTo(chargeTargetFromTEM.getId());
        assertThat(chargeTargetFromRepo.getExternalId()).isEqualTo(chargeTargetFromTEM.getExternalId());
        assertThat(chargeTargetFromRepo.getName()).isEqualTo(chargeTargetFromTEM.getName());

        assertSessionStatementExecutionCount(testEntityManager, 1);
    }

    @Test
    @DisplayName("должен не найти цель списания по несуществующему ID")
    void shouldNotFindChargeTargetById() {
        Optional<ChargeTarget> notExistedChargeTarget = chargeTargetRepository.findById(999L);
        assertThat(notExistedChargeTarget).isEmpty();
    }

    @Test
    @DisplayName("должен найти все цели списания")
    void shouldFindAllCities() {
        List<ChargeTarget> chargeTargets = chargeTargetRepository.findAll();
        assertThat(chargeTargets)
                .isNotEmpty()
                .hasSize(countExistedEntities(testEntityManager, ChargeTarget.class.getName()));
    }

    @Test
    @DisplayName("должен сохранить новую цель списания")
    void shouldSaveChargeTarget() {
        int beforeSave = countExistedEntities(testEntityManager, ChargeTarget.class.getName());

        ChargeTarget newChargeTarget = DummyDataHelper.getDummyChargeTarget();
        assertThat(newChargeTarget.getId()).isNull();

        chargeTargetRepository.save(newChargeTarget);
        assertThat(newChargeTarget.getId()).isNotNull();

        int afterSave = countExistedEntities(testEntityManager, ChargeTarget.class.getName());

        assertThat(beforeSave + 1).isEqualTo(afterSave);
    }

    @Test
    @DisplayName("должен обновить существующую цель списания")
    void shouldUpdateChargeTarget() {
        ChargeTarget chargeTargetToUpdate = testEntityManager.find(ChargeTarget.class, 1L);

        Long newExistedId = chargeTargetToUpdate.getExternalId() + 1L;
        String newChargeTargetName = chargeTargetToUpdate.getName() + UPDATE_SUFFIX;

        chargeTargetToUpdate.setExternalId(newExistedId);
        chargeTargetToUpdate.setName(newChargeTargetName);

        chargeTargetRepository.save(chargeTargetToUpdate);

        ChargeTarget updatedChargeTarget = testEntityManager.find(ChargeTarget.class, 1L);

        assertThat(updatedChargeTarget.getExternalId()).isEqualTo(newExistedId);
        assertThat(updatedChargeTarget.getName()).isEqualTo(newChargeTargetName);
    }

    @Test
    @DisplayName("должен выбросить исключение при удалении используемой цели списания по ID")
    void shouldNotDeleteUsedChargeTargetById() {
        chargeTargetRepository.deleteById(1L);
        assertThatThrownBy(() -> testEntityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("должен удалить не используемую цель списания по ID")
    void shouldDeleteUnusedChargeTargetById() {
        ChargeTarget toDelete = chargeTargetRepository.save(DummyDataHelper.getDummyChargeTarget());

        int beforeDelete = countExistedEntities(testEntityManager, ChargeTarget.class.getName());

        chargeTargetRepository.deleteById(toDelete.getId());

        int afterDelete = countExistedEntities(testEntityManager, ChargeTarget.class.getName());

        assertThat(beforeDelete - 1).isEqualTo(afterDelete);
    }

}
