package com.app.service;

import com.app.exception.ResourceNotFoundException;
import com.app.model.BookingStatus;
import com.app.model.Seat;
import com.app.model.Ticket;
import com.app.model.User;
import com.app.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // ✅ **Get all bookings of a user**
    public List<Ticket> getUserBookings(User user) {
        return ticketRepository.findByUser(user);
    }

    // ✅ **Get a ticket by ID**
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    // ✅ **Book a ticket**
    @Transactional
    public Ticket bookTicket(Ticket ticket) {
        if (ticket.getSeats() == null || ticket.getSeats().isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected for booking.");
        }
        ticket.setStatus(BookingStatus.PENDING); // Mark as pending payment
        return ticketRepository.save(ticket);
    }
    
 // ✅ **Confirm ticket after successful payment**
    @Transactional
    public Ticket confirmTicket(Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
        ticket.setStatus(BookingStatus.CONFIRMED);
        return ticketRepository.save(ticket);
    }

    
    
    // ✅ **Cancel a ticket**
    @Transactional
    public String cancelTicket(Long id) {
        Ticket ticket = getTicketById(id);
        
        // Mark seats as available
        for (Seat seat : ticket.getSeats()) {
            seat.setBooked(false);
            seat.setTicket(null); // Remove ticket reference
        }

        ticketRepository.delete(ticket);
        return "Ticket with ID " + id + " has been cancelled successfully.";
    }

    // ✅ **Get all tickets (Admin feature)**
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}
