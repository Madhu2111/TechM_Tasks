package com.showvault.service;

import com.showvault.model.Booking;
import com.showvault.model.BookingStatus;
import com.showvault.model.SeatBooking;
import com.showvault.model.ShowSchedule;
import com.showvault.models.User;
import com.showvault.repository.BookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TicketService {

    private final BookingRepository bookingRepository;
    
    @Autowired
    public TicketService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
    
    /**
     * Generate a PDF ticket for a booking
     * @param bookingId The ID of the booking
     * @return Byte array containing the PDF ticket
     */
    public byte[] generateTicketPdf(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + bookingId);
        }
        
        Booking booking = bookingOpt.get();
        
        // Only generate tickets for confirmed bookings
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot generate ticket for booking with status: " + booking.getStatus());
        }
        
        return generatePdf(booking);
    }
    
    /**
     * Generate a PDF ticket for a booking by booking number
     * @param bookingNumber The booking number
     * @return Byte array containing the PDF ticket
     */
    public byte[] generateTicketPdfByBookingNumber(String bookingNumber) {
        Optional<Booking> bookingOpt = bookingRepository.findByBookingNumber(bookingNumber);
        
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with number: " + bookingNumber);
        }
        
        Booking booking = bookingOpt.get();
        
        // Only generate tickets for confirmed bookings
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot generate ticket for booking with status: " + booking.getStatus());
        }
        
        return generatePdf(booking);
    }
    
    /**
     * Generate QR code for a booking
     * @param booking The booking
     * @return Byte array containing the QR code image
     */
    public byte[] generateQRCode(Booking booking) {
        // Create QR code content with booking details
        String qrContent = String.format("BOOKING:%s,USER:%d,SCHEDULE:%d",
                booking.getBookingNumber(),
                booking.getUser().getId(),
                booking.getShowSchedule().getId());
        
        // Generate QR code image
        return generateQRCodeImage(qrContent, 200, 200);
    }
    
    /**
     * Generate QR code image
     * @param content The content to encode in the QR code
     * @param width The width of the QR code
     * @param height The height of the QR code
     * @return Byte array containing the QR code image
     */
    private byte[] generateQRCodeImage(String content, int width, int height) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            // Note: This is a placeholder for actual QR code generation
            // In a real implementation, you would use a library like ZXing or QRGen
            // Example with ZXing:
            // BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
            // MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            // For now, we'll return an empty byte array as a placeholder
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }
    
    /**
     * Generate PDF for a booking
     * @param booking The booking
     * @return Byte array containing the PDF
     */
    private byte[] generatePdf(Booking booking) {
        try {
            // Get booking details
            User user = booking.getUser();
            ShowSchedule schedule = booking.getShowSchedule();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            // Create a map of ticket data
            Map<String, Object> ticketData = new HashMap<>();
            ticketData.put("bookingNumber", booking.getBookingNumber());
            ticketData.put("customerName", user.getFirstName() + " " + user.getLastName());
            ticketData.put("showTitle", schedule.getShow().getTitle());
            ticketData.put("venueName", schedule.getVenue().getName());
            ticketData.put("showDate", schedule.getShowDate().format(dateFormatter));
            ticketData.put("showTime", schedule.getStartTime().format(timeFormatter));
            ticketData.put("totalAmount", booking.getTotalAmount().toString());
            
            // Add seat information
            StringBuilder seatsInfo = new StringBuilder();
            for (SeatBooking seatBooking : booking.getSeatBookings()) {
                seatsInfo.append(seatBooking.getSeat().getSeatNumber())
                        .append(" (")
                        .append(seatBooking.getSeat().getSeatType())
                        .append("), ");
            }
            if (seatsInfo.length() > 2) {
                seatsInfo.setLength(seatsInfo.length() - 2); // Remove trailing comma and space
            }
            ticketData.put("seats", seatsInfo.toString());
            
            // Generate QR code
            byte[] qrCode = generateQRCode(booking);
            ticketData.put("qrCode", qrCode);
            
            // Note: This is a placeholder for actual PDF generation
            // In a real implementation, you would use a library like iText or Apache PDFBox
            // For now, we'll return an empty byte array as a placeholder
            return new byte[0];
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF ticket", e);
        }
    }
}