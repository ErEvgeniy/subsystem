package ru.ermolaev.services.notification.sms.sender.service;

import ru.ermolaev.services.notification.models.model.SmsNotification;

public interface SmsNotificationService {

    void sendNotification(SmsNotification notification);

}
