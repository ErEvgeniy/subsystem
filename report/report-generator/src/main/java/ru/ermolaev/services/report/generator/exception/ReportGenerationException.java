package ru.ermolaev.services.report.generator.exception;

import lombok.Getter;

@Getter
public class ReportGenerationException extends RuntimeException {

    private final String code;

    public ReportGenerationException(String code, String message) {
        super(message);
        this.code = code;
    }

}
