package com.showvault.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.showvault.dto.SeatMapDTO;
import com.showvault.dto.SeatMapDTO.SeatDTO;
import com.showvault.dto.SeatMapDTO.SeatRowDTO;
import com.showvault.model.Seat;
import com.showvault.model.ShowSchedule;

@Service
public class SeatMapService {

    @Autowired
    private SeatService seatService;
    
    @Autowired
    private ShowScheduleService showScheduleService;
    
    /**
     * Generates a seat map for a specific show schedule
     * 
     * @param showId The ID of the show
     * @param scheduleId The ID of the show schedule
     * @return A SeatMapDTO containing the seat map
     */
    public SeatMapDTO generateSeatMap(Long showId, Long scheduleId) {
        // Get the show schedule
        ShowSchedule schedule = showScheduleService.getShowScheduleById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Show schedule not found"));
        
        // Get the venue ID
        Long venueId = schedule.getVenue().getId();
        
        // Get all seats for the venue
        List<Seat> allSeats = seatService.getSeatsByVenueId(venueId);
        
        // Get available seats for the show schedule
        List<Seat> availableSeats = seatService.getAvailableSeatsByVenueAndShowSchedule(venueId, scheduleId);
        
        // Create a set of available seat IDs for quick lookup
        final List<Long> availableSeatIds = availableSeats.stream()
                .map(Seat::getId)
                .collect(Collectors.toList());
        
        // Group seats by row
        Map<String, List<Seat>> seatsByRow = allSeats.stream()
                .collect(Collectors.groupingBy(Seat::getRowName));
        
        // Create the seat map
        SeatMapDTO seatMap = new SeatMapDTO("SCREEN");
        
        // Sort rows alphabetically
        List<String> sortedRows = seatsByRow.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
        
        // Create rows
        for (String rowName : sortedRows) {
            SeatRowDTO rowDTO = new SeatRowDTO();
            rowDTO.setRowLabel(rowName);
            
            // Sort seats by seat number
            List<Seat> rowSeats = seatsByRow.get(rowName).stream()
                    .sorted(Comparator.comparing(Seat::getSeatNumber))
                    .collect(Collectors.toList());
            
            // Create seats
            for (Seat seat : rowSeats) {
                SeatDTO seatDTO = new SeatDTO();
                seatDTO.setId(seat.getId());
                seatDTO.setSeatNumber(seat.getSeatNumber());
                
                // Determine seat status
                if (availableSeatIds.contains(seat.getId())) {
                    seatDTO.setStatus("AVAILABLE");
                } else {
                    // If not available, it's either reserved or sold
                    // For simplicity, we'll mark it as SOLD
                    seatDTO.setStatus("SOLD");
                }
                
                // Set seat category
                seatDTO.setCategory(seat.getCategory().name());
                
                // Calculate seat price
                BigDecimal basePrice = schedule.getBasePrice();
                BigDecimal priceMultiplier = seat.getPriceMultiplier();
                BigDecimal seatPrice = basePrice.multiply(priceMultiplier);
                seatDTO.setPrice(seatPrice);
                
                rowDTO.getSeats().add(seatDTO);
            }
            
            seatMap.getRows().add(rowDTO);
        }
        
        return seatMap;
    }
    
    /**
     * Generates a sample seat map for testing
     * 
     * @return A sample SeatMapDTO
     */
    public SeatMapDTO generateSampleSeatMap() {
        SeatMapDTO seatMap = new SeatMapDTO("SCREEN");
        
        // Create rows A-J
        String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        long seatId = 1;
        
        for (String rowLabel : rowLabels) {
            SeatRowDTO rowDTO = new SeatRowDTO();
            rowDTO.setRowLabel(rowLabel);
            
            // Create 15 seats per row
            for (int i = 1; i <= 15; i++) {
                SeatDTO seatDTO = new SeatDTO();
                seatDTO.setId(seatId++);
                seatDTO.setSeatNumber(i);
                
                // Determine seat category based on row
                String category = "STANDARD";
                BigDecimal price = new BigDecimal("10.99");
                
                if (rowLabel.equals("A") || rowLabel.equals("B")) {
                    category = "VIP";
                    price = new BigDecimal("19.99");
                } else if (rowLabel.equals("C") || rowLabel.equals("D") || rowLabel.equals("E")) {
                    category = "PREMIUM";
                    price = new BigDecimal("14.99");
                }
                
                seatDTO.setCategory(category);
                seatDTO.setPrice(price);
                
                // Randomly mark some seats as sold or reserved
                double random = Math.random();
                if (random < 0.2) {
                    seatDTO.setStatus("SOLD");
                } else if (random < 0.3) {
                    seatDTO.setStatus("RESERVED");
                } else {
                    seatDTO.setStatus("AVAILABLE");
                }
                
                rowDTO.getSeats().add(seatDTO);
            }
            
            seatMap.getRows().add(rowDTO);
        }
        
        return seatMap;
    }
}