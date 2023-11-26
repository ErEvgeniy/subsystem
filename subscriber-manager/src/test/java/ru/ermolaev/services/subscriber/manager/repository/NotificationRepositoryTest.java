package ru.ermolaev.services.subscriber.manager.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.ermolaev.services.subscriber.manager.domain.Notification;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.assertSessionStatementExecutionCount;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.clearSessionStatistic;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.countExistedEntities;
import static ru.ermolaev.services.subscriber.manager.util.TestRepositoryUtils.countExistedEntitiesBySubscriber;

@DisplayName("Репозиторий для работы с нотификациями ")
@DataJpaTest
public class NotificationRepositoryTest {

    private static final String UPDATE_SUFFIX = "_upd";

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен получить нотификацию по ID за 1 обращение к БД")
    void shouldFindNotificationById() {
        clearSessionStatistic(testEntityManager);

        Optional<Notification> optionalNotificationFromRepo = notificationRepository.findById(1L);
        Notification notificationFromTEM = testEntityManager.find(Notification.class, 1L);

        assertThat(optionalNotificationFromRepo)
                .isNotEmpty()
                .contains(notificationFromTEM);

        Notification notificationFromRepo = optionalNotificationFromRepo.get();

        assertThat(notificationFromRepo.getId()).isEqualTo(notificationFromTEM.getId());
        assertThat(notificationFromRepo.getSentDate()).isEqualTo(notificationFromTEM.getSentDate());
        assertThat(notificationFromRepo.getStatus()).isEqualTo(notificationFromTEM.getStatus());
        assertThat(notificationFromRepo.getMessage()).isEqualTo(notificationFromTEM.getMessage());
        assertThat(notificationFromRepo.getChannel()).isEqualTo(notificationFromTEM.getChannel());
        assertThat(notificationFromRepo.getDestination()).isEqualTo(notificationFromTEM.getDestination());
        assertThat(notificationFromRepo.getReason()).isEqualTo(notificationFromTEM.getReason());

        assertThat(notificationFromRepo.getSubscriber()).isNotNull();
        assertThat(notificationFromTEM.getSubscriber()).isNotNull();
        assertThat(notificationFromRepo.getSubscriber()).isEqualTo(notificationFromTEM.getSubscriber());
        assertThat(notificationFromRepo.getSubscriber().getFirstname()).isEqualTo(notificationFromTEM.getSubscriber().getFirstname());

        assertSessionStatementExecutionCount(testEntityManager, 1);
    }

    @Test
    @DisplayName("должен не найти нотификацию по несуществующему ID")
    void shouldNotFindNotificationById() {
        Optional<Notification> notExistedNotification = notificationRepository.findById(999L);
        assertThat(notExistedNotification).isEmpty();
    }

    @Test
    @DisplayName("должен найти все нотификации")
    void shouldFindAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications)
                .isNotEmpty()
                .hasSize(countExistedEntities(testEntityManager, Notification.class.getName()));
    }

    @Test
    @DisplayName("должен найти все нотификации конкретного абонента за 1 обращение к БД")
    void shouldFindAllSubscriberNotifications() {
        clearSessionStatistic(testEntityManager);

        List<Notification> notifications = notificationRepository.findAllBySubscriberId(1L);

        notifications.forEach(notification -> assertThat(notification.getSubscriber().getFirstname()).isNotNull());

        assertSessionStatementExecutionCount(testEntityManager, 1);

        assertThat(notifications)
                .isNotEmpty()
                .hasSize(countExistedEntitiesBySubscriber(testEntityManager, Notification.class.getName(), 1));
    }

    @Test
    @DisplayName("должен сохранить новую нотификацию")
    void shouldSaveNotification() {
        int beforeSave = countExistedEntities(testEntityManager, Notification.class.getName());

        Subscriber subscriber = testEntityManager.find(Subscriber.class, 1L);

        Notification newNotification = DummyDataHelper.getDummyNotification();
        newNotification.setSubscriber(subscriber);

        assertThat(newNotification.getId()).isNull();

        notificationRepository.save(newNotification);
        assertThat(newNotification.getId()).isNotNull();

        int afterSave = countExistedEntities(testEntityManager, Notification.class.getName());

        assertThat(beforeSave + 1).isEqualTo(afterSave);
    }

    @Test
    @DisplayName("должен обновить существующую нотификацию")
    void shouldUpdateNotification() {
        Notification notificationToUpdate = testEntityManager.find(Notification.class, 1L);

        LocalDateTime newSentDate = notificationToUpdate.getSentDate().plusDays(1L);
        String newStatus = notificationToUpdate.getStatus() + UPDATE_SUFFIX;
        String newMessage = notificationToUpdate.getMessage() + UPDATE_SUFFIX;
        String newChannel = notificationToUpdate.getChannel() + UPDATE_SUFFIX;
        String newDestination = notificationToUpdate.getDestination() + UPDATE_SUFFIX;
        String newReason = notificationToUpdate.getReason() + UPDATE_SUFFIX;

        notificationToUpdate.setSentDate(newSentDate);
        notificationToUpdate.setStatus(newStatus);
        notificationToUpdate.setMessage(newMessage);
        notificationToUpdate.setChannel(newChannel);
        notificationToUpdate.setDestination(newDestination);
        notificationToUpdate.setReason(newReason);

        notificationRepository.save(notificationToUpdate);

        Notification updatedNotification = testEntityManager.find(Notification.class, 1L);

        assertThat(updatedNotification.getSentDate()).isEqualTo(newSentDate);
        assertThat(updatedNotification.getStatus()).isEqualTo(newStatus);
        assertThat(updatedNotification.getMessage()).isEqualTo(newMessage);
        assertThat(updatedNotification.getChannel()).isEqualTo(newChannel);
        assertThat(updatedNotification.getDestination()).isEqualTo(newDestination);
        assertThat(updatedNotification.getReason()).isEqualTo(newReason);
    }

    @Test
    @DisplayName("должен удалить существующую нотификацию по ID")
    void shouldDeleteNotificationById() {
        int beforeDelete = countExistedEntities(testEntityManager, Notification.class.getName());

        notificationRepository.deleteById(1L);

        int afterDelete = countExistedEntities(testEntityManager, Notification.class.getName());

        assertThat(beforeDelete - 1).isEqualTo(afterDelete);
    }

}
