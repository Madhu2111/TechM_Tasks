package com.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime bookingTime; // When the ticket was booked

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User who booked the ticket

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show; // Show for which the ticket is booked

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Seat> seats; // Seats booked in this ticket

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Payment payment; // Payment details for this ticket

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status; // CONFIRMED, CANCELLED, PENDING

    @Column(nullable = false)
    private double totalPrice; // Total cost of the ticket

    @Column(nullable = false)
    private boolean isActive = true; // Soft delete instead of removing from DB

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Timestamp when ticket was created

    @Column(nullable = false)
    private LocalDateTime updatedAt; // Timestamp when ticket was last updated

    // Auto-set timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.bookingTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
