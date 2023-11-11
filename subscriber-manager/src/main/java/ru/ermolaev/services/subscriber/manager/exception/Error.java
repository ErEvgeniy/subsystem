package ru.ermolaev.services.subscriber.manager.exception;

import lombok.Data;

import java.util.List;

@Data
public class Error {

    private static final long serialVersionUID = -5678154854124804865L;

    private String code;

    private String message;

    private List<ErrorAttribute> errorAttributes;

    public Error(String code) {
        this.code = code;
    }

    public Error(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Error(String code, String message, List<ErrorAttribute> errorAttributes) {
        this.code = code;
        this.message = message;
        this.errorAttributes = errorAttributes;
    }

}
