package ru.ermolaev.services.notificator.sms.service;

import ru.ermolaev.services.notificator.model.ClientNotification;

public interface SmsNotificationService {

    void sendNotification(ClientNotification notification);

}
