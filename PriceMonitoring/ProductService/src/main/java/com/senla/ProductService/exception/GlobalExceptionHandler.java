package com.senla.ProductService.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidation(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(
                new ErrorMessage(
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now().format(formatter),
                        errors
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> handleConstraint(ConstraintViolationException e) {

        Map<String, String> errors = new HashMap<>();

        e.getConstraintViolations().forEach(
                v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));

        return new ResponseEntity<>(
                new ErrorMessage(
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now().format(formatter),
                        errors
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(EntityNotFoundException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.NOT_FOUND, e),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorMessage> handleExists(EntityExistsException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.CONFLICT, e),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgument(IllegalArgumentException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, e),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(CsvImportException.class)
    public ResponseEntity<ErrorMessage> handleCsvImport(CsvImportException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, e),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessage> handleMissing(MissingServletRequestParameterException e) {
        Map<String, String> errors = new HashMap<>();

        errors.put(e.getParameterName(), "Parameter is missing");

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().format(formatter),
                errors
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleMessageNotReadable(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, e),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorMessage> handleMessageNotReadable(InvalidParameterException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, e),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ErrorMessage> handleKafkaException(KafkaException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.INTERNAL_SERVER_ERROR, e),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ErrorMessage buildError(HttpStatus status, Exception e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        errors.put("type", e.getClass().getSimpleName());

        return new ErrorMessage(
                status.value(),
                LocalDateTime.now().format(formatter),
                errors
        );
    }
}
