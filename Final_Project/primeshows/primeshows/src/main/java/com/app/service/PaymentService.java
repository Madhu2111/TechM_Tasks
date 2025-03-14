package com.app.service;

import com.app.dto.PaymentDTO;
import com.app.model.Payment;
import com.app.model.Ticket;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentService {
    Payment getPaymentById(Long paymentId);  // ✅ Fixed method name
    Payment getPaymentByTicket(Ticket ticket);
    
    @Transactional
    PaymentDTO processPayment(PaymentDTO paymentDTO);
    
    @Transactional
    String refundPayment(Long ticketId);
}
