package com.app.service;

import com.app.dto.PaymentDTO;
import com.app.exception.PaymentFailureException;
import com.app.exception.ResourceNotFoundException;
import com.app.model.Booking;
import com.app.model.BookingStatus;
import com.app.model.Payment;
import com.app.model.PaymentMode;
import com.app.model.PaymentStatus;
import com.app.model.Ticket;
import com.app.repository.BookingRepository;
import com.app.repository.PaymentRepository;
import com.app.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository, TicketRepository ticketRepository) {
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
    }

    @Override
    public Payment getPaymentByTicket(Ticket ticket) {
        return paymentRepository.findByTicketId(ticket.getId())  // ✅ Fixed method to use ticket ID
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for the given ticket"));
    }

    // ✅ **Mock Payment Processing**
    @Transactional
    @Override
    public PaymentDTO processPayment(PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTicket(ticketRepository.findById(paymentDTO.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found")));

        paymentRepository.save(payment);
        return new PaymentDTO(payment.getId(),payment.getId(), payment.getAmount(), payment.getPaymentStatus().name());
    }

    
    
    
    
    
    
    // ✅ **Refund Payment (Admin Only)**
    @Transactional
    @Override
    public String refundPayment(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        Payment payment = getPaymentByTicket(ticket);
        if (payment == null || !payment.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            return "Refund not possible. Payment either failed or does not exist.";
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        ticket.setStatus(BookingStatus.REFUNDED);
        ticketRepository.save(ticket);

        return "Refund processed successfully for ticket ID: " + ticketId;
    }
}

