package ru.ermolaev.services.report.generator.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.ermolaev.services.report.generator.exception.ReportGenerationException;
import ru.ermolaev.services.report.generator.exception.Error;

@ControllerAdvice
public class RestExceptionHandler {

    private static final String ERROR_PREFIX = "error.";

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = ReportGenerationException.class)
    public Error handleBusinessExceptions(ReportGenerationException ex) {
        return new Error(ERROR_PREFIX + ex.getCode(), ex.getMessage());
    }

}
