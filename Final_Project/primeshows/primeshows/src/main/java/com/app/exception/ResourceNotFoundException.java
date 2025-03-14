package com.app.exception;

public class ResourceNotFoundException extends RuntimeException {

    // Constructor with a custom message
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Constructor with message and cause
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Default constructor with a generic message
    public ResourceNotFoundException() {
        super("Requested resource not found!");
    }
}
