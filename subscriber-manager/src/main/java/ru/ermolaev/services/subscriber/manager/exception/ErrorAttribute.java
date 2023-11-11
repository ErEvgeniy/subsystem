package ru.ermolaev.services.subscriber.manager.exception;

import lombok.Data;

@Data
public class ErrorAttribute {

    private static final long serialVersionUID = 5472112246580250501L;

    private String attributeName;

    private String description;

    public ErrorAttribute(String attributeName, String description) {
        this.attributeName = attributeName;
        this.description = description;
    }

}
