package com.app.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long bookingId;
    private Long ticketId;  
    private BigDecimal amount;
    private String paymentStatus;
    public String getStatus() {
        return paymentStatus;
    }
    
}


