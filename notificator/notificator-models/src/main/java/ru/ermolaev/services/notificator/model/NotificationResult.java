package ru.ermolaev.services.notificator.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResult {

    private Long notificationId;

    private LocalDateTime sentDate;

    private NotificationStatus status;

    private String reason;

}
