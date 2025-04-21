package com.showvault.controller;

import com.showvault.dto.BookingDTO;
import com.showvault.model.Booking;
import com.showvault.model.BookingStatus;
import com.showvault.model.Seat;
import com.showvault.model.SeatBooking;
import com.showvault.model.Show;
import com.showvault.model.ShowSchedule;
import com.showvault.models.User;
import com.showvault.security.services.UserDetailsImpl;
import com.showvault.service.BookingService;
import com.showvault.service.DTOConverterService;
import com.showvault.service.SeatService;
import com.showvault.service.ShowScheduleService;
import com.showvault.service.ShowService;
import com.showvault.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShowScheduleService showScheduleService;

    @Autowired
    private ShowService showService;

    @Autowired
    private SeatService seatService;
    
    @Autowired
    private DTOConverterService dtoConverterService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingDTO> bookingDTOs = dtoConverterService.convertBookingsToDTO(bookings);
        return new ResponseEntity<>(bookingDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        Optional<Booking> bookingOpt = bookingService.getBookingById(id);
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            
            // Check if the user is authorized to view this booking
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isOrganizer = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"));
            boolean isOwner = booking.getUser().getId().equals(userDetails.getId());
            boolean isShowCreator = isOrganizer && 
                    booking.getShowSchedule().getShow().getCreatedBy().getId().equals(userDetails.getId());
            
            if (isAdmin || isOwner || isShowCreator) {
                BookingDTO bookingDTO = dtoConverterService.convertToBookingDTO(booking);
                return new ResponseEntity<>(bookingDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/number/{bookingNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<Booking> getBookingByNumber(@PathVariable String bookingNumber) {
        Optional<Booking> bookingOpt = bookingService.getBookingByNumber(bookingNumber);
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            
            // Check if the user is authorized to view this booking
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isOwner = booking.getUser().getId().equals(userDetails.getId());
            boolean isShowCreator = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER")) && 
                    booking.getShowSchedule().getShow().getCreatedBy().getId().equals(userDetails.getId());
            
            if (isAdmin || isOwner || isShowCreator) {
                return new ResponseEntity<>(booking, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingDTO>> getMyBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        System.out.println("Getting bookings for user ID: " + userDetails.getId());
        
        // Get the user from the database
        Optional<User> userOpt = userService.getUserById(userDetails.getId());
        if (!userOpt.isPresent()) {
            System.out.println("User not found with ID: " + userDetails.getId());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        
        User user = userOpt.get();
        System.out.println("Found user: " + user.getUsername() + " (ID: " + user.getId() + ")");
        
        List<Booking> bookings = bookingService.getBookingsByUserId(user.getId());
        System.out.println("Found " + bookings.size() + " bookings for user");
        
        // Print details of each booking for debugging
        for (Booking booking : bookings) {
            System.out.println("Booking ID: " + booking.getId() + 
                               ", Number: " + booking.getBookingNumber() + 
                               ", Status: " + booking.getStatus() + 
                               ", User ID: " + (booking.getUser() != null ? booking.getUser().getId() : "null"));
        }
        
        // Convert to DTOs to avoid circular references
        List<BookingDTO> bookingDTOs = bookings.stream()
            .map(booking -> dtoConverterService.convertToBookingDTO(booking))
            .collect(java.util.stream.Collectors.toList());
        
        return new ResponseEntity<>(bookingDTOs, HttpStatus.OK);
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Booking>> getRecentBookings(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        List<Booking> bookings = bookingService.getRecentBookingsByUserId(userDetails.getId(), fromDate);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/schedule/{scheduleId}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getBookingsBySchedule(@PathVariable Long scheduleId) {
        // Check if the user is authorized to view these bookings
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<ShowSchedule> scheduleOpt = showScheduleService.getShowScheduleById(scheduleId);
        if (!scheduleOpt.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        ShowSchedule schedule = scheduleOpt.get();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isShowCreator = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER")) && 
                schedule.getShow().getCreatedBy().getId().equals(userDetails.getId());
        
        if (isAdmin || isShowCreator) {
            List<Booking> bookings = bookingService.getBookingsByShowScheduleId(scheduleId);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/show/{showId}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getBookingsByShow(@PathVariable Long showId) {
        // Check if the user is authorized to view these bookings
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<Show> showOpt = showService.getShowById(showId);
        if (!showOpt.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Show show = showOpt.get();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isShowCreator = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER")) && 
                show.getCreatedBy().getId().equals(userDetails.getId());
        
        if (isAdmin || isShowCreator) {
            List<Booking> bookings = bookingService.getBookingsByShowId(showId);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/schedule/{scheduleId}/seats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createBooking(
            @PathVariable Long scheduleId,
            @RequestBody List<Long> seatIds) {
        
        if (seatIds == null || seatIds.isEmpty()) {
            return new ResponseEntity<>("No seats selected", HttpStatus.BAD_REQUEST);
        }
        
        // Log the received seat IDs for debugging
        System.out.println("Received seat IDs: " + seatIds);
        
        // Get the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userService.getUserById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        
        // Get the show schedule
        Optional<ShowSchedule> scheduleOpt = showScheduleService.getShowScheduleById(scheduleId);
        if (!scheduleOpt.isPresent()) {
            return new ResponseEntity<>("Show schedule not found", HttpStatus.NOT_FOUND);
        }
        
        // Get the seats
        List<Seat> seats = seatService.getSeatsByIds(seatIds);
        if (seats.size() != seatIds.size()) {
            return new ResponseEntity<>("One or more seats not found", HttpStatus.NOT_FOUND);
        }
        
        // Check if seats are available
        List<Seat> availableSeats = seatService.getAvailableSeatsByVenueAndShowSchedule(
                scheduleOpt.get().getVenue().getId(), scheduleId);
        
        if (!availableSeats.containsAll(seats)) {
            return new ResponseEntity<>("One or more seats are not available", HttpStatus.BAD_REQUEST);
        }
        
        // Create the booking
        try {
            Booking booking = bookingService.createBooking(userOpt.get(), scheduleOpt.get(), seats);
            System.out.println("Created booking with ID: " + booking.getId() + " for user ID: " + userOpt.get().getId());
            
            // Verify the booking was saved correctly
            Optional<Booking> savedBooking = bookingService.getBookingById(booking.getId());
            if (savedBooking.isPresent()) {
                System.out.println("Successfully retrieved saved booking with ID: " + booking.getId());
                
                // Ensure all relationships are loaded
                Booking fullBooking = savedBooking.get();
                
                // Force initialization of lazy-loaded collections
                if (fullBooking.getSeatBookings() != null) {
                    System.out.println("Booking has " + fullBooking.getSeatBookings().size() + " seat bookings");
                    
                    // Force initialization of each seat in seat bookings
                    for (SeatBooking sb : fullBooking.getSeatBookings()) {
                        if (sb.getSeat() != null) {
                            System.out.println("Seat booking has seat ID: " + sb.getSeat().getId());
                        }
                    }
                }
                
                // Convert to DTO to avoid circular references
                BookingDTO bookingDTO = dtoConverterService.convertToBookingDTO(fullBooking);
                return new ResponseEntity<>(bookingDTO, HttpStatus.CREATED);
            } else {
                System.out.println("WARNING: Could not retrieve saved booking with ID: " + booking.getId());
                return new ResponseEntity<>(booking, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Failed to create booking: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get available seats for a show schedule in seat map format
     * 
     * @param scheduleId The ID of the show schedule
     * @return The seat map with availability information
     */
    @GetMapping("/schedules/{scheduleId}/seats")
    public ResponseEntity<?> getAvailableSeatsForSchedule(@PathVariable Long scheduleId) {
        try {
            // Redirect to the seat map controller
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .header("Location", "/api/seat-maps/shows/0/schedules/" + scheduleId)
                    .build();
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to get available seats: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> updateBookingStatus(
            @PathVariable Long id, 
            @RequestParam BookingStatus status) {
        
        Optional<Booking> bookingOpt = bookingService.getBookingById(id);
        if (!bookingOpt.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Booking booking = bookingOpt.get();
        
        // Check if the user is authorized to update this booking
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = booking.getUser().getId().equals(userDetails.getId());
        
        // Users can only cancel their own bookings
        if (isOwner && status != BookingStatus.CANCELLED) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        if (isAdmin || isOwner) {
            Optional<Booking> updatedBookingOpt = bookingService.updateBookingStatus(id, status);
            return updatedBookingOpt.isPresent() ? 
                    new ResponseEntity<>(HttpStatus.OK) : 
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get saved payment methods for the current user
     * 
     * @return List of payment methods
     */
    @GetMapping("/payment-methods")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSavedPaymentMethods() {
        try {
            // Get the current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // In a real implementation, this would fetch payment methods from a database
            // For now, return mock payment methods
            List<Map<String, Object>> paymentMethods = new ArrayList<>();
            
            // Add a mock credit card
            Map<String, Object> creditCard = new HashMap<>();
            creditCard.put("id", "pm_" + UUID.randomUUID().toString().substring(0, 8));
            creditCard.put("type", "CREDIT_CARD");
            creditCard.put("name", "Visa ending in 4242");
            creditCard.put("icon", "bi-credit-card");
            creditCard.put("lastFour", "4242");
            creditCard.put("expiryDate", "12/25");
            paymentMethods.add(creditCard);
            
            // Add a mock PayPal account
            Map<String, Object> paypal = new HashMap<>();
            paypal.put("id", "pm_" + UUID.randomUUID().toString().substring(0, 8));
            paypal.put("type", "PAYPAL");
            paypal.put("name", "PayPal Account");
            paypal.put("icon", "bi-paypal");
            paymentMethods.add(paypal);
            
            return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching payment methods: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Add a new payment method for the current user
     * 
     * @param paymentMethod The payment method to add
     * @return The added payment method with an ID
     */
    @PostMapping("/payment-methods")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addPaymentMethod(@RequestBody Map<String, Object> paymentMethod) {
        try {
            // Get the current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // In a real implementation, this would save the payment method to a database
            // For now, just add an ID and return it
            paymentMethod.put("id", "pm_" + UUID.randomUUID().toString().substring(0, 8));
            
            return new ResponseEntity<>(paymentMethod, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding payment method: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Process payment for a booking
     * 
     * @param bookingId The ID of the booking to process payment for
     * @param paymentRequest The payment request containing payment method ID
     * @return The payment intent
     */
    @PostMapping("/{bookingId}/payment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> processPayment(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> paymentRequest) {
        
        try {
            // Get the current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Get the booking
            Optional<Booking> bookingOpt = bookingService.getBookingById(bookingId);
            if (!bookingOpt.isPresent()) {
                return new ResponseEntity<>("Booking not found", HttpStatus.NOT_FOUND);
            }
            
            Booking booking = bookingOpt.get();
            
            // Check if the user is authorized to process this payment
            if (!booking.getUser().getId().equals(userDetails.getId())) {
                return new ResponseEntity<>("You are not authorized to process this payment", HttpStatus.FORBIDDEN);
            }
            
            // Check if the booking is in a valid state for payment
            if (booking.getStatus() != BookingStatus.PENDING) {
                return new ResponseEntity<>("This booking is not in a valid state for payment", HttpStatus.BAD_REQUEST);
            }
            
            // Get the payment method ID
            String paymentMethodId = paymentRequest.get("paymentMethodId");
            if (paymentMethodId == null || paymentMethodId.isEmpty()) {
                return new ResponseEntity<>("Payment method ID is required", HttpStatus.BAD_REQUEST);
            }
            
            // Process the payment (in a real implementation, this would integrate with a payment gateway)
            // For now, create a mock payment intent
            Map<String, Object> paymentIntent = new HashMap<>();
            paymentIntent.put("id", "pi_" + UUID.randomUUID().toString().substring(0, 8));
            paymentIntent.put("bookingId", bookingId);
            paymentIntent.put("amount", booking.getTotalAmount());
            paymentIntent.put("currency", "USD");
            paymentIntent.put("status", "COMPLETED");
            paymentIntent.put("paymentMethodId", paymentMethodId);
            paymentIntent.put("createdAt", LocalDateTime.now());
            
            // Update the booking status
            bookingService.updateBookingStatus(bookingId, BookingStatus.CONFIRMED);
            
            return new ResponseEntity<>(paymentIntent, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing payment: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get booking notifications for the current user
     * 
     * @return List of notifications
     */
    @GetMapping("/notifications")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getBookingNotifications() {
        try {
            // Get the current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // In a real implementation, this would fetch notifications from a database
            // For now, return mock notifications
            List<Map<String, Object>> notifications = new ArrayList<>();
            
            // Add a mock notification
            Map<String, Object> notification1 = new HashMap<>();
            notification1.put("id", 1);
            notification1.put("title", "Booking Confirmed");
            notification1.put("message", "Your booking has been confirmed.");
            notification1.put("date", LocalDateTime.now().minusHours(2));
            notification1.put("read", false);
            notification1.put("type", "BOOKING_CONFIRMED");
            notifications.add(notification1);
            
            // Add another mock notification
            Map<String, Object> notification2 = new HashMap<>();
            notification2.put("id", 2);
            notification2.put("title", "Show Reminder");
            notification2.put("message", "Your show is starting in 24 hours.");
            notification2.put("date", LocalDateTime.now().minusDays(1));
            notification2.put("read", true);
            notification2.put("type", "SHOW_REMINDER");
            notifications.add(notification2);
            
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching notifications: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Mark a notification as read
     * 
     * @param notificationId The ID of the notification to mark as read
     * @return Success message
     */
    @PutMapping("/notifications/{notificationId}/read")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            // Get the current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // In a real implementation, this would update the notification in a database
            // For now, just return success
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification marked as read");
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error marking notification as read: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}