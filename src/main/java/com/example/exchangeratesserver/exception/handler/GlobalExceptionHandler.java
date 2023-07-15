package com.example.exchangeratesserver.exception.handler;

import com.example.exchangeratesserver.client.exception.IllegalStartEndDate;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

//отправка ошибок при http-запросах по стандарту RFC 7807
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalStartEndDate.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse onIllegalStartEndDate(final IllegalStartEndDate e) {
        logger.warn("Неправильная дата начала и конца: {}", e);
        return ErrorResponse.create(e, BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse onConstraintViolationException(final ConstraintViolationException e) {
        logger.info("Ошибка валидации: ", e);

        List<BindingError> bindingErrors = new ArrayList<>(e.getConstraintViolations().size());
        e.getConstraintViolations().forEach(cv -> {
            String propertyPath = cv.getPropertyPath().toString();
            String name = propertyPath.substring(propertyPath.lastIndexOf(".") + 1);

            bindingErrors.add(new BindingError(name, cv.getMessage()));
        });

        return ErrorResponse.builder(e, BAD_REQUEST, "Ошибка валидации")
                .property("errors", bindingErrors)
                .build();
    }

    @ExceptionHandler(Throwable.class)
    public ErrorResponse onThrowable(final Throwable e) {
        logger.error("Ошибка сервера: ", e);
        return ErrorResponse.create(e, INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
