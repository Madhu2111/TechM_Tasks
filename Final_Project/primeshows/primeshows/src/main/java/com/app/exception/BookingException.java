package com.app.exception;

public class BookingException extends RuntimeException {

    // Constructor with a custom message
    public BookingException(String message) {
        super(message);
    }

    // Constructor with message and cause
    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }

    // Default constructor with a generic message
    public BookingException() {
        super("Booking process failed due to an error!");
    }
}
