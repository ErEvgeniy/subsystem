package ru.ermolaev.services.notification.models.model;

import lombok.Getter;
import lombok.Setter;
import ru.ermolaev.services.notification.models.constant.NotificationStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResult {

    private static final long serialVersionUID = 751566504573791901L;

    private Long notificationId;

    private LocalDateTime sentDate;

    private NotificationStatus status;

    private String reason;

}
