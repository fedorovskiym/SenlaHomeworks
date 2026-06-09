package com.senla.UserService.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
