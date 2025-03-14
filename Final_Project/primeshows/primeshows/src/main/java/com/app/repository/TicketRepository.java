package com.app.repository;

import com.app.model.Show;
import com.app.model.Ticket;
import com.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByUser(User user);  // Get tickets booked by a specific user

    List<Ticket> findByShow(Show show);  // Get all tickets booked for a specific show
    
    Optional<Ticket> findByUserAndShow(User user, Show show); // Check if a user has already booked a ticket for a show
}
