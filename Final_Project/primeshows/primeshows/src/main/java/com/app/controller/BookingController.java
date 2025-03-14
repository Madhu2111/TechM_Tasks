package com.app.controller;

import com.app.dto.BookingDTO;
import com.app.model.Booking;
import com.app.service.BookingService;
import com.app.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> bookTicket(@RequestBody BookingDTO bookingDto) {
        try {
            Booking booking = bookingService.bookTicket(bookingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Booking failed: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return (booking != null) ? ResponseEntity.ok(booking)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsByUserId(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return (!bookings.isEmpty()) ? ResponseEntity.ok(bookings)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("No bookings found for the user");
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok("Booking canceled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking cancellation failed: " + e.getMessage());
        }
    }
}

