package ru.ermolaev.services.subscriber.manager.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.ermolaev.services.subscriber.manager.domain.Street;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.assertSessionStatementExecutionCount;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.clearSessionStatistic;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.countExistedEntities;

@DisplayName("Репозиторий для работы с улицами ")
@DataJpaTest
public class StreetRepositoryTest {

    private static final String UPDATE_SUFFIX = "_upd";

    @Autowired
    private StreetRepository streetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен получить улицу по ID за 1 обращение к БД")
    void shouldFindStreetById() {
        clearSessionStatistic(testEntityManager);

        Optional<Street> optionalStreetFromRepo = streetRepository.findById(1L);
        Street streetFromTEM = testEntityManager.find(Street.class, 1L);

        assertThat(optionalStreetFromRepo)
                .isNotEmpty()
                .contains(streetFromTEM);

        Street streetFromRepo = optionalStreetFromRepo.get();

        assertThat(streetFromRepo.getId()).isEqualTo(streetFromTEM.getId());
        assertThat(streetFromRepo.getExternalId()).isEqualTo(streetFromTEM.getExternalId());
        assertThat(streetFromRepo.getName()).isEqualTo(streetFromTEM.getName());

        assertSessionStatementExecutionCount(testEntityManager, 1);
    }

    @Test
    @DisplayName("должен не найти улицу по несуществующему ID")
    void shouldNotFindStreetById() {
        Optional<Street> notExistedStreet = streetRepository.findById(999L);
        assertThat(notExistedStreet).isEmpty();
    }

    @Test
    @DisplayName("должен найти все улицы")
    void shouldFindAllStreets() {
        List<Street> streets = streetRepository.findAll();
        assertThat(streets)
                .isNotEmpty()
                .hasSize(countExistedEntities(testEntityManager, Street.class.getName()));
    }

    @Test
    @DisplayName("должен сохранить новую улицу")
    void shouldSaveStreet() {
        int beforeSave = countExistedEntities(testEntityManager, Street.class.getName());

        Street newStreet = DummyDataHelper.getDummyStreet();
        assertThat(newStreet.getId()).isNull();

        streetRepository.save(newStreet);
        assertThat(newStreet.getId()).isNotNull();

        int afterSave = countExistedEntities(testEntityManager, Street.class.getName());

        assertThat(beforeSave + 1).isEqualTo(afterSave);
    }

    @Test
    @DisplayName("должен обновить существующую улицу")
    void shouldUpdateStreet() {
        Street streetToUpdate = testEntityManager.find(Street.class, 1L);

        Long newExistedId = streetToUpdate.getExternalId() + 1L;
        String newStreetName = streetToUpdate.getName() + UPDATE_SUFFIX;

        streetToUpdate.setExternalId(newExistedId);
        streetToUpdate.setName(newStreetName);

        streetRepository.save(streetToUpdate);

        Street updatedStreet = testEntityManager.find(Street.class, 1L);

        assertThat(updatedStreet.getExternalId()).isEqualTo(newExistedId);
        assertThat(updatedStreet.getName()).isEqualTo(newStreetName);
    }

    @Test
    @DisplayName("должен выбросить исключение при удалении используемой улицы по ID")
    void shouldNotDeleteUsedStreetById() {
        streetRepository.deleteById(1L);
        assertThatThrownBy(() -> testEntityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("должен удалить не используемую улицу по ID")
    void shouldDeleteUnusedStreetById() {
        Street toDelete = streetRepository.save(DummyDataHelper.getDummyStreet());

        int beforeDelete = countExistedEntities(testEntityManager, Street.class.getName());

        streetRepository.deleteById(toDelete.getId());

        int afterDelete = countExistedEntities(testEntityManager, Street.class.getName());

        assertThat(beforeDelete - 1).isEqualTo(afterDelete);
    }

}
