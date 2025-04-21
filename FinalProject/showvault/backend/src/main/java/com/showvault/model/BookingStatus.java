package com.showvault.model;

/**
 * Enum representing the possible states of a booking
 */
public enum BookingStatus {
    /**
     * Booking has been created but not yet confirmed (e.g., payment pending)
     */
    PENDING,
    
    /**
     * Booking has been confirmed (e.g., payment received)
     */
    CONFIRMED,
    
    /**
     * Booking has been cancelled by the user or system
     */
    CANCELLED,
    
    /**
     * Booking has been completed (e.g., event has passed)
     */
    COMPLETED,
    
    /**
     * Booking has been refunded
     */
    REFUNDED
}