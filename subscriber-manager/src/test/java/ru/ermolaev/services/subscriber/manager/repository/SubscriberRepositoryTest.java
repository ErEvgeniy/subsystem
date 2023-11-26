package ru.ermolaev.services.subscriber.manager.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.ermolaev.services.subscriber.manager.domain.City;
import ru.ermolaev.services.subscriber.manager.domain.Street;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.assertSessionStatementExecutionCount;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.clearSessionStatistic;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.countExistedEntities;

@DisplayName("Репозиторий для работы с абонентами ")
@DataJpaTest
public class SubscriberRepositoryTest {

    private static final String UPDATE_SUFFIX = "_upd";

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен получить абонента по ID за 1 обращение к БД")
    void shouldFindSubscriberById() {
        clearSessionStatistic(testEntityManager);

        Optional<Subscriber> optionalSubscriberFromRepo = subscriberRepository.findById(1L);
        Subscriber subscriberFromTEM = testEntityManager.find(Subscriber.class, 1L);

        assertThat(optionalSubscriberFromRepo)
                .isNotEmpty()
                .contains(subscriberFromTEM);

        Subscriber subscriberFromRepo = optionalSubscriberFromRepo.get();

        assertThat(subscriberFromRepo.getId()).isEqualTo(subscriberFromTEM.getId());
        assertThat(subscriberFromRepo.getExternalId()).isEqualTo(subscriberFromTEM.getExternalId());
        assertThat(subscriberFromRepo.getFirstname()).isEqualTo(subscriberFromTEM.getFirstname());
        assertThat(subscriberFromRepo.getPatronymic()).isEqualTo(subscriberFromTEM.getPatronymic());
        assertThat(subscriberFromRepo.getLastname()).isEqualTo(subscriberFromTEM.getLastname());
        assertThat(subscriberFromRepo.getContractNumber()).isEqualTo(subscriberFromTEM.getContractNumber());
        assertThat(subscriberFromRepo.getAccountNumber()).isEqualTo(subscriberFromTEM.getAccountNumber());

        assertThat(subscriberFromRepo.getCity()).isNotNull();
        assertThat(subscriberFromTEM.getCity()).isNotNull();
        assertThat(subscriberFromRepo.getCity()).isEqualTo(subscriberFromTEM.getCity());
        assertThat(subscriberFromRepo.getCity().getId()).isEqualTo(subscriberFromTEM.getCity().getId());

        assertThat(subscriberFromRepo.getStreet()).isNotNull();
        assertThat(subscriberFromTEM.getStreet()).isNotNull();
        assertThat(subscriberFromRepo.getStreet()).isEqualTo(subscriberFromTEM.getStreet());
        assertThat(subscriberFromRepo.getStreet().getId()).isEqualTo(subscriberFromTEM.getStreet().getId());

        assertThat(subscriberFromRepo.getHouse()).isEqualTo(subscriberFromTEM.getHouse());
        assertThat(subscriberFromRepo.getFlat()).isEqualTo(subscriberFromTEM.getFlat());
        assertThat(subscriberFromRepo.getPhoneNumber()).isEqualTo(subscriberFromTEM.getPhoneNumber());
        assertThat(subscriberFromRepo.getEmail()).isEqualTo(subscriberFromTEM.getEmail());
        assertThat(subscriberFromRepo.getBalance()).isEqualTo(subscriberFromTEM.getBalance());
        assertThat(subscriberFromRepo.getIsActive()).isEqualTo(subscriberFromTEM.getIsActive());
        assertThat(subscriberFromRepo.getConnectionDate()).isEqualTo(subscriberFromTEM.getConnectionDate());

        assertSessionStatementExecutionCount(testEntityManager, 1);
    }

    @Test
    @DisplayName("должен не найти абонента по несуществующему ID")
    void shouldNotFindSubscriberById() {
        Optional<Subscriber> notExistedSubscriber = subscriberRepository.findById(999L);
        assertThat(notExistedSubscriber).isEmpty();
    }

    @Test
    @DisplayName("должен найти всех абонентов")
    void shouldFindAllSubscribers() {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        assertThat(subscribers)
                .isNotEmpty()
                .hasSize(countExistedEntities(testEntityManager, Subscriber.class.getName()));
    }

    @Test
    @DisplayName("должен сохранить нового абонента")
    void shouldSaveSubscriber() {
        int beforeSave = countExistedEntities(testEntityManager, Subscriber.class.getName());

        City city = testEntityManager.find(City.class, 1L);
        Street street = testEntityManager.find(Street.class, 1L);

        Subscriber newSubscriber = DummyDataHelper.getDummySubscriber();
        newSubscriber.setCity(city);
        newSubscriber.setStreet(street);

        assertThat(newSubscriber.getId()).isNull();

        subscriberRepository.save(newSubscriber);
        assertThat(newSubscriber.getId()).isNotNull();

        int afterSave = countExistedEntities(testEntityManager, Subscriber.class.getName());

        assertThat(beforeSave + 1).isEqualTo(afterSave);
    }

    @Test
    @DisplayName("должен обновить существующего абонента")
    void shouldUpdateSubscriber() {
        Subscriber subscriberToUpdate = testEntityManager.find(Subscriber.class, 1L);

        Long newExistedId = subscriberToUpdate.getExternalId() + 1L;
        String newFirstname = subscriberToUpdate.getFirstname() + UPDATE_SUFFIX;
        String newPatronymic = subscriberToUpdate.getPatronymic() + UPDATE_SUFFIX;
        String newLastname = subscriberToUpdate.getLastname() + UPDATE_SUFFIX;
        String newContractNumber = subscriberToUpdate.getContractNumber() + UPDATE_SUFFIX;
        String newAccountNumber = subscriberToUpdate.getAccountNumber() + UPDATE_SUFFIX;
        Integer newHouse = subscriberToUpdate.getHouse() + 1;
        Integer newFlat = subscriberToUpdate.getFlat() + 1;
        String newPhoneNumber = subscriberToUpdate.getPhoneNumber() + UPDATE_SUFFIX;
        String newEmail = subscriberToUpdate.getEmail() + UPDATE_SUFFIX;
        Boolean newIsActive = !subscriberToUpdate.getIsActive();
        LocalDate newConnectionDate = subscriberToUpdate.getConnectionDate().plusDays(1);

        subscriberToUpdate.setExternalId(newExistedId);
        subscriberToUpdate.setFirstname(newFirstname);
        subscriberToUpdate.setPatronymic(newPatronymic);
        subscriberToUpdate.setLastname(newLastname);
        subscriberToUpdate.setContractNumber(newContractNumber);
        subscriberToUpdate.setAccountNumber(newAccountNumber);
        subscriberToUpdate.setHouse(newHouse);
        subscriberToUpdate.setFlat(newFlat);
        subscriberToUpdate.setPhoneNumber(newPhoneNumber);
        subscriberToUpdate.setEmail(newEmail);
        subscriberToUpdate.setIsActive(newIsActive);
        subscriberToUpdate.setConnectionDate(newConnectionDate);

        subscriberRepository.save(subscriberToUpdate);

        Subscriber updatedSubscriber = testEntityManager.find(Subscriber.class, 1L);

        assertThat(updatedSubscriber.getExternalId()).isEqualTo(newExistedId);
        assertThat(updatedSubscriber.getFirstname()).isEqualTo(newFirstname);
        assertThat(updatedSubscriber.getPatronymic()).isEqualTo(newPatronymic);
        assertThat(updatedSubscriber.getLastname()).isEqualTo(newLastname);
        assertThat(updatedSubscriber.getContractNumber()).isEqualTo(newContractNumber);
        assertThat(updatedSubscriber.getAccountNumber()).isEqualTo(newAccountNumber);
        assertThat(updatedSubscriber.getHouse()).isEqualTo(newHouse);
        assertThat(updatedSubscriber.getFlat()).isEqualTo(newFlat);
        assertThat(updatedSubscriber.getPhoneNumber()).isEqualTo(newPhoneNumber);
        assertThat(updatedSubscriber.getEmail()).isEqualTo(newEmail);
        assertThat(updatedSubscriber.getIsActive()).isEqualTo(newIsActive);
        assertThat(updatedSubscriber.getConnectionDate()).isEqualTo(newConnectionDate);
    }

    @Test
    @DisplayName("должен выбросить исключение при удалении используемого абонента по ID")
    void shouldNotDeleteUsedSubscriberById() {
        subscriberRepository.deleteById(1L);
        assertThatThrownBy(() -> testEntityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("должен удалить не используемого абонента по ID")
    void shouldDeleteUnusedSubscriberById() {
        City city = testEntityManager.find(City.class, 1L);
        Street street = testEntityManager.find(Street.class, 1L);

        Subscriber toDelete = DummyDataHelper.getDummySubscriber();
        toDelete.setCity(city);
        toDelete.setStreet(street);

        subscriberRepository.save(toDelete);

        int beforeDelete = countExistedEntities(testEntityManager, Subscriber.class.getName());

        subscriberRepository.deleteById(toDelete.getId());

        int afterDelete = countExistedEntities(testEntityManager, Subscriber.class.getName());

        assertThat(beforeDelete - 1).isEqualTo(afterDelete);
    }

}
