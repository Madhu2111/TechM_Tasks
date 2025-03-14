package com.app.service;

import com.app.dto.BookingDTO;
import com.app.model.Booking;

import java.util.List;

public interface BookingService {
    Booking bookTicket(BookingDTO bookingDTO);
    Booking getBookingById(Long id);
    List<Booking> getAllBookings();
    List<Booking> getBookingsByUserId(Long userId);
    void cancelBooking(Long id);
}
