package com.ecommerce.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ValidationErrorResponse extends ErrorDetails {
    private Map<String, String> validationErrors;

    public ValidationErrorResponse(LocalDateTime timestamp, String message, String path, String errorCode, Map<String, String> validationErrors) {
        super(timestamp, message, path, errorCode);
        this.validationErrors = validationErrors;
    }
}