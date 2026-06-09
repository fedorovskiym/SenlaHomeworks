package com.senla.UserService.exception;

public class KafkaException extends RuntimeException {
    public KafkaException(String message) {
        super(message);
    }
}
