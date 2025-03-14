package com.app.repository;

import com.app.model.Payment;
import com.app.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
      

    List<Payment> findByPaymentStatus(String status);  // Get payments by status (SUCCESS, PENDING, FAILED)

    List<Payment> findByAmountBetween(double minAmount, double maxAmount);  // Get payments in a specific price range

    long countByPaymentStatus(String status);  // Count payments by status
    
    Optional<Payment> findByTicketId(Long ticketId);// Get payment details for a specific booking
}
