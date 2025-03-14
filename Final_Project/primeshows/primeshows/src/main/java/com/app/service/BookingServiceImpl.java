package com.app.service;

import com.app.dto.BookingDTO;
import com.app.exception.ResourceNotFoundException;
import com.app.model.Booking;
import com.app.model.Show;
import com.app.model.User;
import com.app.repository.BookingRepository;
import com.app.repository.ShowRepository;
import com.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ShowRepository showRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ShowRepository showRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.showRepository = showRepository;
    }

    @Override
    public Booking bookTicket(BookingDTO bookingDTO) {
        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Show show = showRepository.findById(bookingDTO.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setNumberOfSeats(bookingDTO.getNumberOfSeats());
        booking.setBookingTime(LocalDateTime.now());
        booking.setTotalAmount(bookingDTO.getTotalAmount());

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        bookingRepository.delete(booking);
    }
}
