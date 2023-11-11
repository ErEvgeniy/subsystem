package ru.ermolaev.services.subscriber.manager.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonException extends RuntimeException {

    private String message;

    public CommonException(String message) {
        this.message = message;
    }

}
