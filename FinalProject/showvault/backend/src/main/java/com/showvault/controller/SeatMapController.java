package com.showvault.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.showvault.dto.SeatMapDTO;
import com.showvault.service.SeatMapService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/seat-maps")
public class SeatMapController {

    @Autowired
    private SeatMapService seatMapService;
    
    /**
     * Get the seat map for a specific show schedule
     * 
     * @param showId The ID of the show
     * @param scheduleId The ID of the show schedule
     * @return The seat map
     */
    @GetMapping("/shows/{showId}/schedules/{scheduleId}")
    public ResponseEntity<SeatMapDTO> getSeatMap(
            @PathVariable Long showId,
            @PathVariable Long scheduleId) {
        try {
            SeatMapDTO seatMap = seatMapService.generateSeatMap(showId, scheduleId);
            return new ResponseEntity<>(seatMap, HttpStatus.OK);
        } catch (Exception e) {
            // For development/testing, return a sample seat map
            SeatMapDTO sampleSeatMap = seatMapService.generateSampleSeatMap();
            return new ResponseEntity<>(sampleSeatMap, HttpStatus.OK);
        }
    }
    
    /**
     * Get a sample seat map for testing
     * 
     * @return A sample seat map
     */
    @GetMapping("/sample")
    public ResponseEntity<SeatMapDTO> getSampleSeatMap() {
        SeatMapDTO seatMap = seatMapService.generateSampleSeatMap();
        return new ResponseEntity<>(seatMap, HttpStatus.OK);
    }
}