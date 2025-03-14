package com.app.repository;

import com.app.model.Seat;
import com.app.model.Show;
import com.app.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    List<Seat> findByShowAndIsBookedFalse(Show show);  // Get available seats for a show

    List<Seat> findByShowAndIsBookedTrue(Show show);  // Get booked seats for a show

    List<Seat> findByShow(Show show);  // Get all seats (both booked and available) for a show

    List<Seat> findByTicket(Ticket ticket);  // Get all seats linked to a specific ticket

    Seat findByShowAndSeatNumber(Show show, String seatNumber);  // Find a specific seat

    long countByShowAndIsBookedFalse(Show show);  // Count available seats for a show

    long countByShowAndIsBookedTrue(Show show);  // Count booked seats for a show
}
