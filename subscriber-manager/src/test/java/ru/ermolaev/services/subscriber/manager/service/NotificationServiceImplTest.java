package ru.ermolaev.services.subscriber.manager.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import ru.ermolaev.services.notification.models.constant.NotificationStatus;
import ru.ermolaev.services.notification.models.model.NotificationResult;
import ru.ermolaev.services.subscriber.manager.domain.Notification;
import ru.ermolaev.services.subscriber.manager.mapper.NotificationMapperImpl;
import ru.ermolaev.services.subscriber.manager.property.LocaleProvider;
import ru.ermolaev.services.subscriber.manager.repository.NotificationRepository;
import ru.ermolaev.services.subscriber.manager.repository.SubscriberRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.NotificationDto;
import ru.ermolaev.services.subscriber.manager.service.impl.NotificationServiceImpl;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Сервис по работе с нотификациями ")
@SpringBootTest(classes = {NotificationServiceImpl.class, NotificationMapperImpl.class})
public class NotificationServiceImplTest {

    private static final Long NOTIFICATION_ID = 1L;

    private static final Long SUBSCRIBER_ID = 1L;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private MessageSource messageSource;

    @MockBean
    private LocaleProvider localeProvider;

    @MockBean
    private SubscriberRepository subscriberRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationServiceImpl notificationService;

    @Test
    @DisplayName("должен сохранить результат отправки нотификации")
    public void shouldSaveNotificationResult() {
        Notification dummyNotification = DummyDataHelper.getDummyNotification();
        dummyNotification.setId(NOTIFICATION_ID);

        NotificationResult result = new NotificationResult();
        result.setNotificationId(dummyNotification.getId());
        result.setStatus(NotificationStatus.SENT);
        result.setSentDate(LocalDateTime.now());

        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.of(dummyNotification));
        when(notificationRepository.save(dummyNotification)).thenReturn(dummyNotification);

        notificationService.acceptNotificationResult(result);

        verify(notificationRepository, times(1)).findById(NOTIFICATION_ID);
        verify(notificationRepository, times(1)).save(dummyNotification);
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке сохранить результат отправки нотификации")
    public void shouldThrowExceptionWhileAcceptNotificationResult() {
        Notification dummyNotification = DummyDataHelper.getDummyNotification();
        dummyNotification.setId(NOTIFICATION_ID);

        NotificationResult result = new NotificationResult();
        result.setNotificationId(dummyNotification.getId());

        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.empty());
        when(notificationRepository.save(dummyNotification)).thenReturn(dummyNotification);

        assertThatThrownBy(() -> notificationService.acceptNotificationResult(result))
                .isInstanceOf(EntityNotFoundException.class);

        verify(notificationRepository, times(1)).findById(NOTIFICATION_ID);
        verify(notificationRepository, times(0)).save(dummyNotification);
    }

    @Test
    @DisplayName("должен найти все нотификации принадлежащие конкретному абоненту")
    public void shouldFindAllNotificationsBySubscriberId() {
        when(notificationRepository.findAllBySubscriberId(SUBSCRIBER_ID)).thenReturn(List.of(DummyDataHelper.getDummyNotification()));

        List<NotificationDto> paymentDtoList = notificationService.findAllBySubscriberId(SUBSCRIBER_ID);

        assertThat(paymentDtoList)
                .isNotNull()
                .hasSize(1);

        verify(notificationRepository, times(1)).findAllBySubscriberId(SUBSCRIBER_ID);
    }

}
