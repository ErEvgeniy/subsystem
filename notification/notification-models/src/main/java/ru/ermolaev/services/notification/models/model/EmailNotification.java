package ru.ermolaev.services.notification.models.model;

import lombok.Data;

@Data
public class EmailNotification implements ClientNotification {

    private static final long serialVersionUID = -976157150152450013L;

    private Long id;

    private String email;

    private String subject;

    private String message;

    private byte[] attachment;

}
