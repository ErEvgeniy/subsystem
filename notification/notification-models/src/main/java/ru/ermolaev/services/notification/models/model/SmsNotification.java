package ru.ermolaev.services.notification.models.model;

import lombok.Data;

@Data
public class SmsNotification implements ClientNotification {

    private static final long serialVersionUID = 166986015776775129L;

    private Long id;

    private String phoneNumber;

    private String message;

}
