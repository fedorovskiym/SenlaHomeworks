package com.senla.UserService.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorMessage> handleExists(EntityExistsException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.CONFLICT, e),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(EntityNotFoundException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.NOT_FOUND, e),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> handleBadCredentials(BadCredentialsException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.UNAUTHORIZED, e),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleBadCredentials(UsernameNotFoundException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.NOT_FOUND, e),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorMessage> handleBadCredentials(AuthException e) {
        return new ResponseEntity<>(
                buildError(HttpStatus.NOT_FOUND, e),
                HttpStatus.NOT_FOUND
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
