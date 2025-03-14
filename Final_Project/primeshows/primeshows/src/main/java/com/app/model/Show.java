package com.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;  // Show name

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;  // Enum instead of String (Action, Drama, Comedy)

    @Column(nullable = false, length = 50)
    private String language;

    @Column(nullable = false)
    private LocalDateTime showTime;

    @Column(nullable = false, length = 200)
    private String venue;
    
    @Column(nullable = false)
    private double price;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;  // Show hosted by an organizer

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Seat> seats;  // Available seats

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShowStatus status;  // ACTIVE, CANCELLED, COMPLETED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // Timestamp when show was created

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();  // Timestamp when show was last updated

    @Column(nullable = false)
    private boolean isActive = true;  // Soft delete flag

    // Automatically set timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
