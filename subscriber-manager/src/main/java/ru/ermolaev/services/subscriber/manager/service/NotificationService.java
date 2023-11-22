package ru.ermolaev.services.subscriber.manager.service;

import ru.ermolaev.services.notification.models.model.NotificationResult;
import ru.ermolaev.services.subscriber.manager.rest.dto.NotificationDto;

import java.util.List;

public interface NotificationService {

    void notifySubscriber(long subscriberId, String templateName, String channel);

    void acceptNotificationResult(NotificationResult notificationResult);

    List<NotificationDto> findAllBySubscriberId(long id);

}
