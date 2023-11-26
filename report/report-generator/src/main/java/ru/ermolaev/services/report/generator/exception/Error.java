package ru.ermolaev.services.report.generator.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {

    private static final long serialVersionUID = 4935595665012432051L;

    private String code;

    private String message;

}
