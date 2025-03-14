package com.app.exception;

public class UserAlreadyExistsException extends RuntimeException {
    
    // Constructor with a custom message
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    // Constructor with message and cause
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    // Default constructor with a standard message
    public UserAlreadyExistsException() {
        super("User already exists!");
    }
}
