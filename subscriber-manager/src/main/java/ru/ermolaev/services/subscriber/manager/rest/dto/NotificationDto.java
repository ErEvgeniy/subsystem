package ru.ermolaev.services.subscriber.manager.rest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {

    private static final long serialVersionUID = 4577451697815070057L;

    private Long id;

    private LocalDateTime sentDate;

    private Long subscriberId;

    private String status;

    private String message;

    private String channel;

    private String destination;

    private String reason;

}
