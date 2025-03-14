package com.app.service;

import com.app.exception.ResourceNotFoundException;
import com.app.model.Seat;
import com.app.model.Show;
import com.app.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    // ✅ **Get available seats for a show**
    public List<Seat> getAvailableSeats(Show show) {
        if (show == null) {
            throw new IllegalArgumentException("Show cannot be null.");
        }
        return seatRepository.findByShowAndIsBookedFalse(show);
    }

    // ✅ **Get booked seats for a show**
    public List<Seat> getBookedSeats(Show show) {
        if (show == null) {
            throw new IllegalArgumentException("Show cannot be null.");
        }
        return seatRepository.findByShowAndIsBookedTrue(show);
    }

    // ✅ **Book a seat (with validation)**
    @Transactional
    public Seat bookSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + seatId));

        if (seat.isBooked()) {
            throw new IllegalStateException("This seat is already booked.");
        }

        seat.setBooked(true);
        return seatRepository.save(seat);
    }

    // ✅ **Release a booked seat (for cancellations)**
    @Transactional
    public Seat releaseSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + seatId));

        if (!seat.isBooked()) {
            throw new IllegalStateException("This seat is not booked yet.");
        }

        seat.setBooked(false);
        return seatRepository.save(seat);
    }
}
