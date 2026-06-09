package com.senla.UserService.exception;

import java.util.Map;

public class ErrorMessage {

    private int statusCode;
    private String timestamp;
    private Map<String, String> message;

    public ErrorMessage(int statusCode, String timestamp, Map<String, String> message) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getMessage() {
        return message;
    }


    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setMessage(Map<String, String> message) {
        this.message = message;
    }
}