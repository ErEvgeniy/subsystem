package ru.ermolaev.services.subscriber.manager.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.ermolaev.services.subscriber.manager.exception.BusinessException;
import ru.ermolaev.services.subscriber.manager.exception.Error;
import ru.ermolaev.services.subscriber.manager.exception.ErrorAttribute;
import ru.ermolaev.services.subscriber.manager.exception.ValidationException;

import java.util.List;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    private static final String VALIDATION_ERROR_CODE = "validation.Error";

    private static final String BUSINESS_ERROR_CODE = "business.Error";

    private void logError(Throwable ex) {
        log.error("Exception occurred: ", ex);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = BusinessException.class)
    public Error handleBusinessExceptions(BusinessException ex) {
        logError(ex);
        return new Error(BUSINESS_ERROR_CODE, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ValidationException.class)
    public Error handleValidationExceptions(ValidationException ex) {
        logError(ex);
        return new Error(VALIDATION_ERROR_CODE, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Error handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException ex) {
        logError(ex);
        BindingResult bindingResult = ex.getBindingResult();
        Error validationError = new Error(VALIDATION_ERROR_CODE);
        List<ErrorAttribute> errorAttributes = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorAttribute(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        validationError.setErrorAttributes(errorAttributes);
        return validationError;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public void handleUnexpectedExceptions(Throwable ex) {
        logError(ex);
    }

}
