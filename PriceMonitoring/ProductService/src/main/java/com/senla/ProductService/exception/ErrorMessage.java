package com.senla.ProductService.exception;

import java.util.Date;
import java.util.Map;

public class ErrorMessage {

    private int statusCode;
    private Date timestamp;
    private Map<String, String> message;

    public ErrorMessage(int statusCode, Date timestamp, Map<String, String> message) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getMessage() {
        return message;
    }


    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setMessage(Map<String, String> message) {
        this.message = message;
    }
}