package com.github.lfyuomr.gylo.bostongene.task2.web;

import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Component
public class BadRequestExceptionsHandler {
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handle(ConstraintViolationException e) {
        val errors = e.getConstraintViolations()
                      .stream()
                      .map(ConstraintViolation::getMessage)
                      .collect(Collectors.toList());
        return Collections.singletonMap("errors", errors);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handle(MethodArgumentNotValidException e) {
        val errors = e.getBindingResult()
                      .getFieldErrors()
                      .stream()
                      .map(FieldError::getDefaultMessage)
                      .collect(Collectors.toList());
        return Collections.singletonMap("errors", errors);
    }

}
