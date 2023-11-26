package ru.ermolaev.services.notificator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientNotification {

    private Long id;

    private String message;

    private String destination;

}
