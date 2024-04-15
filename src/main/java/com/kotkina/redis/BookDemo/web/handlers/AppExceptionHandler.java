package com.kotkina.redis.BookDemo.web.handlers;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.text.MessageFormat;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class AppExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest webRequest) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleEntityNotFoundException(Exception ex, WebRequest webRequest) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest webRequest) {
        ex.printStackTrace();
        String allErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> MessageFormat.format("Поле `{0}` {1}.", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("\n"));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(allErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(ConstraintViolationException ex, WebRequest webRequest) {
        ex.printStackTrace();
        String allErrors = ex.getConstraintViolations().stream()
                .map(violation -> MessageFormat.format("Поле `{0}` {1}.",
                        Iterables.getLast(Splitter.on(".").split(violation.getPropertyPath().toString())),
                        violation.getMessage()))
                .collect(Collectors.joining("\n"));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(allErrors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleEntityNotFoundException(HttpRequestMethodNotSupportedException ex, WebRequest webRequest) {
        ex.printStackTrace();
        return ResponseEntity.status(ex.getStatusCode())
                .body(ex.getLocalizedMessage());
    }
}
