package ru.ermolaev.services.subscriber.manager.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.ermolaev.services.subscriber.manager.domain.City;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.assertSessionStatementExecutionCount;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.clearSessionStatistic;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.countExistedEntities;

@DisplayName("Репозиторий для работы с городами ")
@DataJpaTest
public class CityRepositoryTest {

    private static final String UPDATE_SUFFIX = "_upd";

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен получить город по ID за 1 обращение к БД")
    void shouldFindCityById() {
        clearSessionStatistic(testEntityManager);

        Optional<City> optionalCityFromRepo = cityRepository.findById(1L);
        City cityFromTEM = testEntityManager.find(City.class, 1L);

        assertThat(optionalCityFromRepo)
                .isNotEmpty()
                .contains(cityFromTEM);

        City cityFromRepo = optionalCityFromRepo.get();

        assertThat(cityFromRepo.getId()).isEqualTo(cityFromTEM.getId());
        assertThat(cityFromRepo.getExternalId()).isEqualTo(cityFromTEM.getExternalId());
        assertThat(cityFromRepo.getName()).isEqualTo(cityFromTEM.getName());

        assertSessionStatementExecutionCount(testEntityManager, 1);
    }

    @Test
    @DisplayName("должен не найти город по несуществующему ID")
    void shouldNotFindCityById() {
        Optional<City> notExistedCity = cityRepository.findById(999L);
        assertThat(notExistedCity).isEmpty();
    }

    @Test
    @DisplayName("должен найти все города")
    void shouldFindAllCities() {
        List<City> cities = cityRepository.findAll();
        assertThat(cities)
                .isNotEmpty()
                .hasSize(countExistedEntities(testEntityManager, City.class.getName()));
    }

    @Test
    @DisplayName("должен сохранить новый город")
    void shouldSaveCity() {
        int beforeSave = countExistedEntities(testEntityManager, City.class.getName());

        City newCity = DummyDataHelper.getDummyCity();
        assertThat(newCity.getId()).isNull();

        cityRepository.save(newCity);
        assertThat(newCity.getId()).isNotNull();

        int afterSave = countExistedEntities(testEntityManager, City.class.getName());

        assertThat(beforeSave + 1).isEqualTo(afterSave);
    }

    @Test
    @DisplayName("должен обновить существующий город")
    void shouldUpdateCity() {
        City cityToUpdate = testEntityManager.find(City.class, 1L);

        Long newExistedId = cityToUpdate.getExternalId() + 1L;
        String newCityName = cityToUpdate.getName() + UPDATE_SUFFIX;

        cityToUpdate.setExternalId(newExistedId);
        cityToUpdate.setName(newCityName);

        cityRepository.save(cityToUpdate);

        City updatedCity = testEntityManager.find(City.class, 1L);

        assertThat(updatedCity.getExternalId()).isEqualTo(newExistedId);
        assertThat(updatedCity.getName()).isEqualTo(newCityName);
    }

    @Test
    @DisplayName("должен выбросить исключение при удалении используемого города по ID")
    void shouldNotDeleteUsedCityById() {
        cityRepository.deleteById(1L);
        assertThatThrownBy(() -> testEntityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("должен удалить не используемый город по ID")
    void shouldDeleteUnusedCityById() {
        City toDelete = cityRepository.save(DummyDataHelper.getDummyCity());

        int beforeDelete = countExistedEntities(testEntityManager, City.class.getName());

        cityRepository.deleteById(toDelete.getId());

        int afterDelete = countExistedEntities(testEntityManager, City.class.getName());

        assertThat(beforeDelete - 1).isEqualTo(afterDelete);
    }

}
