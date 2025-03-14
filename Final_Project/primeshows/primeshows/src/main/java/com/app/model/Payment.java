package com.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMode paymentMode;

    @Column(nullable = false, updatable = false)
    private LocalDateTime paymentTime = LocalDateTime.now();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id", nullable = false, unique = true)
    private Ticket ticket;
    @OneToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    private Booking booking;
    

    public void setPaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

}
