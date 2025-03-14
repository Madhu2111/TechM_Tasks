package com.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"show_id", "seatNumber"})
}) // Ensures seat numbers are unique per show
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String seatNumber;  // e.g., A1, B5

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;  // REGULAR, PREMIUM, VIP

    @Column(nullable = false)
    private boolean isBooked = false;  // Default: Available

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;  // Show to which this seat belongs

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;  // Linked to a ticket if booked

    @Column(nullable = false)
    private boolean isActive = true; // Soft delete flag

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Timestamp when seat was created

    @Column(nullable = false)
    private LocalDateTime updatedAt; // Timestamp when seat was last updated

    // Auto-set timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    public boolean isBooked() {
        return isBooked;
    }

}
