package com.showvault.service;

import com.showvault.dto.BookingDTO;
import com.showvault.dto.SeatBookingDTO;
import com.showvault.dto.SeatDTO;
import com.showvault.dto.ShowDTO;
import com.showvault.dto.ShowScheduleDTO;
import com.showvault.model.Booking;
import com.showvault.model.Seat;
import com.showvault.model.SeatBooking;
import com.showvault.model.Show;
import com.showvault.model.ShowSchedule;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DTOConverterService {

    public SeatDTO convertToDTO(Seat seat) {
        return new SeatDTO(seat);
    }
    
    public List<SeatDTO> convertToDTO(List<Seat> seats) {
        return seats.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public SeatBookingDTO convertToDTO(SeatBooking seatBooking) {
        return new SeatBookingDTO(seatBooking);
    }
    
    public List<SeatBookingDTO> convertSeatBookingsToDTO(List<SeatBooking> seatBookings) {
        return seatBookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public BookingDTO convertToBookingDTO(Booking booking) {
        return new BookingDTO(booking);
    }
    
    public List<BookingDTO> convertBookingsToDTO(List<Booking> bookings) {
        return bookings.stream()
                .map(this::convertToBookingDTO)
                .collect(Collectors.toList());
    }
    
    public ShowDTO convertToShowDTO(Show show) {
        return new ShowDTO(show);
    }
    
    public List<ShowDTO> convertShowsToDTO(List<Show> shows) {
        return shows.stream()
                .map(this::convertToShowDTO)
                .collect(Collectors.toList());
    }
    
    public ShowScheduleDTO convertToShowScheduleDTO(ShowSchedule schedule) {
        return new ShowScheduleDTO(schedule);
    }
    
    public List<ShowScheduleDTO> convertShowSchedulesToDTO(List<ShowSchedule> schedules) {
        return schedules.stream()
                .map(this::convertToShowScheduleDTO)
                .collect(Collectors.toList());
    }
}