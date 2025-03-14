package com.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;  // Encrypted password will be stored

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // USER, ADMIN, ORGANIZER

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ticket> bookings;  // User’s bookings

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // Timestamp when the user was created

    @Column(nullable = false)
    private LocalDateTime updatedAt;  // Timestamp when the user was last updated

    @Column(nullable = false)
    private boolean isActive = true;  // Soft delete flag

    public boolean isActive() {
    	return this.isActive;
    }
    
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
