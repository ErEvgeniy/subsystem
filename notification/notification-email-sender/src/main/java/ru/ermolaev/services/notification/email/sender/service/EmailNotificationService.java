package ru.ermolaev.services.notification.email.sender.service;

import ru.ermolaev.services.notification.models.model.EmailNotification;

public interface EmailNotificationService {

    void sendNotification(EmailNotification notification);

}
