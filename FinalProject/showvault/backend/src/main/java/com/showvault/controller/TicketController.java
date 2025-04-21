package com.showvault.controller;

import com.showvault.model.Booking;
import com.showvault.model.BookingStatus;
import com.showvault.security.services.UserDetailsImpl;
import com.showvault.service.BookingService;
import com.showvault.service.TicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private BookingService bookingService;
    
    /**
     * Download ticket PDF for a booking
     * @param bookingId The ID of the booking
     * @return PDF file as response
     */
    @GetMapping("/download/{bookingId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Long bookingId) {
        // Check if the user is authorized to download this ticket
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<Booking> bookingOpt = bookingService.getBookingById(bookingId);
        
        if (bookingOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Booking booking = bookingOpt.get();
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = booking.getUser().getId().equals(userDetails.getId());
        
        if (!isAdmin && !isOwner) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        // Check if booking is confirmed
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        try {
            byte[] pdfBytes = ticketService.generateTicketPdf(bookingId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "ticket-" + booking.getBookingNumber() + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Download ticket PDF by booking number
     * @param bookingNumber The booking number
     * @return PDF file as response
     */
    @GetMapping("/download/number/{bookingNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadTicketByNumber(@PathVariable String bookingNumber) {
        // Check if the user is authorized to download this ticket
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<Booking> bookingOpt = bookingService.getBookingByNumber(bookingNumber);
        
        if (bookingOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Booking booking = bookingOpt.get();
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = booking.getUser().getId().equals(userDetails.getId());
        
        if (!isAdmin && !isOwner) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        // Check if booking is confirmed
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        try {
            byte[] pdfBytes = ticketService.generateTicketPdfByBookingNumber(bookingNumber);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "ticket-" + booking.getBookingNumber() + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}