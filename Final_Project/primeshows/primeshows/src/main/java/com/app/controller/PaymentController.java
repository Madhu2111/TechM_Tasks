package com.app.controller;

import com.app.dto.PaymentDTO;
import com.app.model.Payment;
import com.app.service.PaymentService;
import com.app.service.TicketService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final TicketService ticketService;

    public PaymentController(PaymentService paymentService, TicketService ticketService) {
        this.paymentService = paymentService;
        this.ticketService = ticketService;
    }

 // ✅ **Process Payment (Mock)**
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<String> processPayment(@RequestBody PaymentDTO paymentDto) {
        try {
            PaymentDTO processedPayment = paymentService.processPayment(paymentDto);
            
            
            
            
            

            if ("SUCCESS".equals(processedPayment.getStatus())) {
            	
                ticketService.confirmTicket(paymentDto.getTicketId()); // ✅ Confirm ticket on success
                return ResponseEntity.status(HttpStatus.CREATED).body("Payment successful! Ticket Confirmed.");
            } else {
                return ResponseEntity.badRequest().body("Payment failed! Please try again.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Payment processing error: " + e.getMessage());
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable Long paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);  // ✅ Fixed incorrect method name
        return (payment != null) ? ResponseEntity.ok(payment)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
    }
 // ✅ **Request Refund (Admin Only)**
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/refund/{ticketId}")
    public ResponseEntity<String> refundPayment(@PathVariable Long ticketId) {
        String response = paymentService.refundPayment(ticketId);
        return ResponseEntity.ok(response);
    }
}
