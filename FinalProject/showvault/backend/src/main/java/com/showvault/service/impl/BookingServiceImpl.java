package com.showvault.service.impl;

import com.showvault.model.Booking;
import com.showvault.model.BookingStatus;
import com.showvault.model.Seat;
import com.showvault.model.SeatBooking;
import com.showvault.model.ShowSchedule;
import com.showvault.models.User;
import com.showvault.repository.BookingRepository;
import com.showvault.repository.SeatBookingRepository;
import com.showvault.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SeatBookingRepository seatBookingRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, SeatBookingRepository seatBookingRepository) {
        this.bookingRepository = bookingRepository;
        this.seatBookingRepository = seatBookingRepository;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> getBookingById(Long id) {
        System.out.println("Getting booking by ID: " + id);
        
        // Try to get the booking with all details
        Optional<Booking> bookingOpt = bookingRepository.findBookingWithDetailsById(id);
        
        // If not found with details, try the standard findById
        if (!bookingOpt.isPresent()) {
            System.out.println("Booking not found with details, trying standard findById");
            bookingOpt = bookingRepository.findById(id);
        }
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            
            // Force initialization of lazy-loaded collections
            if (booking.getSeatBookings() != null) {
                System.out.println("Booking has " + booking.getSeatBookings().size() + " seat bookings");
            }
            
            if (booking.getUser() != null) {
                System.out.println("Booking has user ID: " + booking.getUser().getId());
            }
            
            if (booking.getShowSchedule() != null) {
                System.out.println("Booking has show schedule ID: " + booking.getShowSchedule().getId());
                
                if (booking.getShowSchedule().getShow() != null) {
                    System.out.println("Show schedule has show ID: " + booking.getShowSchedule().getShow().getId());
                }
            }
        } else {
            System.out.println("Booking not found with ID: " + id);
        }
        
        return bookingOpt;
    }

    @Override
    public Optional<Booking> getBookingByNumber(String bookingNumber) {
        return bookingRepository.findByBookingNumber(bookingNumber);
    }

    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        System.out.println("BookingServiceImpl: Getting bookings for user ID: " + userId);
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        System.out.println("BookingServiceImpl: Found " + bookings.size() + " bookings");
        return bookings;
    }

    @Override
    public List<Booking> getBookingsByShowScheduleId(Long showScheduleId) {
        return bookingRepository.findByShowScheduleId(showScheduleId);
    }

    @Override
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
    public List<Booking> getRecentBookingsByUserId(Long userId, LocalDateTime fromDate) {
        return bookingRepository.findRecentBookingsByUserId(userId, fromDate);
    }

    @Override
    public List<Booking> getBookingsByShowId(Long showId) {
        return bookingRepository.findBookingsByShowId(showId);
    }

    @Override
    public Long countConfirmedBookingsByShowScheduleId(Long showScheduleId) {
        return bookingRepository.countConfirmedBookingsByShowScheduleId(showScheduleId);
    }

    @Override
    @Transactional
    public Booking createBooking(User user, ShowSchedule showSchedule, List<Seat> seats) {
        System.out.println("Creating booking for user ID: " + user.getId() + ", schedule ID: " + showSchedule.getId());
        
        // Create a new booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowSchedule(showSchedule);
        booking.setBookingNumber(generateBookingNumber());
        booking.setStatus(BookingStatus.CONFIRMED); // Set status to CONFIRMED immediately for demo
        booking.setBookingDate(LocalDateTime.now());
        
        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Save the booking first to get an ID
        Booking savedBooking = bookingRepository.save(booking);
        System.out.println("Saved booking with ID: " + savedBooking.getId());
        
        // Create seat bookings
        for (Seat seat : seats) {
            SeatBooking seatBooking = new SeatBooking();
            seatBooking.setBooking(savedBooking);
            seatBooking.setSeat(seat);
            
            // Calculate seat price based on base price and seat category multiplier
            BigDecimal seatPrice = showSchedule.getBasePrice().multiply(seat.getPriceMultiplier());
            seatBooking.setPrice(seatPrice);
            
            // Add to total amount
            totalAmount = totalAmount.add(seatPrice);
            
            // Save seat booking
            seatBookingRepository.save(seatBooking);
            System.out.println("Saved seat booking for seat ID: " + seat.getId());
        }
        
        // Update total amount
        savedBooking.setTotalAmount(totalAmount);
        Booking finalBooking = bookingRepository.save(savedBooking);
        System.out.println("Updated booking with total amount: " + totalAmount);
        
        // Verify the booking was saved correctly
        Optional<Booking> verifiedBooking = bookingRepository.findById(finalBooking.getId());
        if (verifiedBooking.isPresent()) {
            System.out.println("Successfully verified booking with ID: " + finalBooking.getId());
            
            // Verify the user relationship
            User bookingUser = verifiedBooking.get().getUser();
            if (bookingUser != null) {
                System.out.println("Booking has user ID: " + bookingUser.getId());
            } else {
                System.out.println("WARNING: Booking has no user associated with it!");
            }
            
            // Verify we can find the booking by user ID
            List<Booking> userBookings = bookingRepository.findByUserId(user.getId());
            System.out.println("Found " + userBookings.size() + " bookings for user ID: " + user.getId());
        } else {
            System.out.println("WARNING: Could not verify booking with ID: " + finalBooking.getId());
        }
        
        return finalBooking;
    }

    @Override
    @Transactional
    public Optional<Booking> updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(newStatus);
            return Optional.of(bookingRepository.save(booking));
        }
        
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public List<Booking> getBookingsWithFilters(int offset, int limit, String status, LocalDate date) {
        // In a real implementation, this would use a repository method with filters
        // For now, we'll simulate filtering with a basic implementation
        
        List<Booking> allBookings = bookingRepository.findAll();
        List<Booking> filteredBookings = new ArrayList<>();
        
        for (Booking booking : allBookings) {
            boolean statusMatch = status == null || booking.getStatus().toString().equals(status);
            boolean dateMatch = date == null || booking.getCreatedAt().toLocalDate().equals(date);
            
            if (statusMatch && dateMatch) {
                filteredBookings.add(booking);
            }
        }
        
        // Apply pagination
        int end = Math.min(offset + limit, filteredBookings.size());
        if (offset >= filteredBookings.size()) {
            return new ArrayList<>();
        }
        
        return filteredBookings.subList(offset, end);
    }

    @Override
    public long countBookingsWithFilters(String status, LocalDate date) {
        // In a real implementation, this would use a repository method with filters
        // For now, we'll simulate counting with a basic implementation
        
        List<Booking> allBookings = bookingRepository.findAll();
        long count = 0;
        
        for (Booking booking : allBookings) {
            boolean statusMatch = status == null || booking.getStatus().toString().equals(status);
            boolean dateMatch = date == null || booking.getCreatedAt().toLocalDate().equals(date);
            
            if (statusMatch && dateMatch) {
                count++;
            }
        }
        
        return count;
    }

    @Override
    @Transactional
    public Optional<Booking> processRefund(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            
            // Check if booking is eligible for refund
            if (booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.PENDING) {
                // In a real implementation, this would process the refund through a payment gateway
                
                // Update booking status
                booking.setStatus(BookingStatus.REFUNDED);
                
                // Save updated booking
                return Optional.of(bookingRepository.save(booking));
            }
        }
        
        return Optional.empty();
    }
    
    private String generateBookingNumber() {
        // Generate a unique booking number
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}