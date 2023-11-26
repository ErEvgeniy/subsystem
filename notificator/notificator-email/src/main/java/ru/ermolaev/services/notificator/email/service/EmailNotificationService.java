package ru.ermolaev.services.notificator.email.service;

import ru.ermolaev.services.notificator.model.ClientNotification;

public interface EmailNotificationService {

    void sendNotification(ClientNotification notification);

}
