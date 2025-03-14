package com.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    private int numberOfSeats;
    private LocalDateTime bookingTime;
    private Double totalAmount;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;
}
