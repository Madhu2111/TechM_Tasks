package com.showvault.repository;

import com.showvault.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    List<Seat> findByVenueId(Long venueId);
    
    List<Seat> findByVenueIdAndCategory(Long venueId, Seat.SeatCategory category);
    
    @Query("SELECT s FROM Seat s WHERE s.venue.id = ?1 AND s.rowName = ?2")
    List<Seat> findByVenueIdAndRowName(Long venueId, String rowName);
    
    @Query("SELECT DISTINCT s.rowName FROM Seat s WHERE s.venue.id = ?1 ORDER BY s.rowName")
    List<String> findAllRowsByVenueId(Long venueId);
    
    @Query("SELECT s FROM Seat s WHERE s.venue.id = ?1 AND s.id NOT IN "
           + "(SELECT sb.seat.id FROM SeatBooking sb WHERE sb.booking.showSchedule.id = ?2 AND sb.booking.status = 'CONFIRMED')")
    List<Seat> findAvailableSeatsByVenueAndShowSchedule(Long venueId, Long showScheduleId);
}