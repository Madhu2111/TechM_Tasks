package com.app.exception;

public class PaymentFailureException extends RuntimeException {

    // Constructor with a custom message
    public PaymentFailureException(String message) {
        super(message);
    }

    // Constructor with message and cause
    public PaymentFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    // Default constructor with a generic message
    public PaymentFailureException() {
        super("Payment process failed! Please try again.");
    }
}
