package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long showId;
    private Long userId;
    private int numberOfSeats;
    private LocalDateTime bookingTime;
    private Double totalAmount;
}
