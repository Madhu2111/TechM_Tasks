package com.showvault.service;

import com.showvault.model.Seat;
import com.showvault.model.Venue;
import com.showvault.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    @Autowired
    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }

    public List<Seat> getSeatsByVenueId(Long venueId) {
        return seatRepository.findByVenueId(venueId);
    }

    public List<Seat> getSeatsByVenueIdAndCategory(Long venueId, Seat.SeatCategory category) {
        return seatRepository.findByVenueIdAndCategory(venueId, category);
    }

    public List<Seat> getSeatsByVenueIdAndRowName(Long venueId, String rowName) {
        return seatRepository.findByVenueIdAndRowName(venueId, rowName);
    }

    public List<String> getAllRowsByVenueId(Long venueId) {
        return seatRepository.findAllRowsByVenueId(venueId);
    }

    public List<Seat> getAvailableSeatsByVenueAndShowSchedule(Long venueId, Long showScheduleId) {
        return seatRepository.findAvailableSeatsByVenueAndShowSchedule(venueId, showScheduleId);
    }

    @Transactional
    public Seat createSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @Transactional
    public List<Seat> createSeatsForVenue(Venue venue, String rowName, int startSeatNumber, int endSeatNumber, 
                                         Seat.SeatCategory category, BigDecimal priceMultiplier) {
        List<Seat> seats = seatRepository.findByVenueIdAndRowName(venue.getId(), rowName);
        
        for (int seatNumber = startSeatNumber; seatNumber <= endSeatNumber; seatNumber++) {
            final int currentSeatNumber = seatNumber;
            boolean seatExists = seats.stream()
                    .anyMatch(s -> s.getSeatNumber() == currentSeatNumber);
            
            if (!seatExists) {
                Seat seat = new Seat();
                seat.setVenue(venue);
                seat.setRowName(rowName);
                seat.setSeatNumber(seatNumber);
                seat.setCategory(category);
                seat.setPriceMultiplier(priceMultiplier);
                seatRepository.save(seat);
                seats.add(seat);
            }
        }
        
        return seats;
    }

    @Transactional
    public Seat updateSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @Transactional
    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }

    public List<Seat> getSeatsByIds(List<Long> seatIds) {
        return seatRepository.findAllById(seatIds);
    }
}