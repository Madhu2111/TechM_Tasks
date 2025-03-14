package com.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Resource Not Found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        logger.warn("User Already Exists: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<Map<String, String>> handleBookingException(BookingException ex) {
        logger.warn("Booking Error: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentFailureException.class)
    public ResponseEntity<Map<String, String>> handlePaymentFailure(PaymentFailureException ex) {
        logger.error("Payment Failure: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.PAYMENT_REQUIRED);
    }

    // Handle invalid method arguments (e.g., missing required fields)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("Validation Error: {}", ex.getMessage());
        Map<String, String> errors = ex.getBindingResult()
                                       .getFieldErrors()
                                       .stream()
                                       .collect(Collectors.toMap(error -> error.getField(), error -> error.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        logger.error("Unexpected Error: {}", ex.getMessage());
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Utility method to build error responses
    private ResponseEntity<Map<String, String>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return new ResponseEntity<>(response, status);
    }
}

