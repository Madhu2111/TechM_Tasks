package com.app.repository;

import com.app.model.Role;
import com.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);  // Find user by email for authentication

    boolean existsByEmail(String email); // Check if an email is already registered

    List<User> findByRole(Role role); // Get users by role (e.g., ORGANIZER, ADMIN, USER)
    
}
