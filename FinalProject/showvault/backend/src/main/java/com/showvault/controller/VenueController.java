package com.showvault.controller;

import com.showvault.model.Venue;
import com.showvault.repository.VenueRepository;
import com.showvault.repository.SeatRepository;
import com.showvault.service.VenueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final VenueService venueService;

    public VenueController(VenueRepository venueRepository, SeatRepository seatRepository, VenueService venueService) {
        this.venueRepository = venueRepository;
        this.seatRepository = seatRepository;
        this.venueService = venueService;
    }

    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        List<Venue> venues = venueRepository.findAll();
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        return venueRepository.findById(id)
                .map(venue -> new ResponseEntity<>(venue, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Venue>> getVenuesByCity(@PathVariable String city) {
        List<Venue> venues = venueService.getVenuesByCity(city);
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<Venue>> getVenuesByCountry(@PathVariable String country) {
        List<Venue> venues = venueService.getVenuesByCountry(country);
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }

    @GetMapping("/capacity/{capacity}")
    public ResponseEntity<List<Venue>> getVenuesByMinimumCapacity(@PathVariable Integer capacity) {
        List<Venue> venues = venueService.getVenuesByMinimumCapacity(capacity);
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        List<String> cities = venueService.getAllCities();
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @GetMapping("/countries")
    public ResponseEntity<List<String>> getAllCountries() {
        List<String> countries = venueService.getAllCountries();
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<?> createVenue(@RequestBody Venue venue) {
        if (venue.getName() == null || venue.getName().isEmpty()) {
            return new ResponseEntity<>("Venue name is required", HttpStatus.BAD_REQUEST);
        }
        if (venue.getCity() == null || venue.getCity().isEmpty()) {
            return new ResponseEntity<>("City is required", HttpStatus.BAD_REQUEST);
        }
        if (venue.getCountry() == null || venue.getCountry().isEmpty()) {
            return new ResponseEntity<>("Country is required", HttpStatus.BAD_REQUEST);
        }
        if (venue.getCapacity() == null || venue.getCapacity() <= 0) {
            return new ResponseEntity<>("Capacity must be positive", HttpStatus.BAD_REQUEST);
        }
        
        Venue newVenue = venueService.createVenue(venue);
        return new ResponseEntity<>(newVenue, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<?> updateVenue(@PathVariable Long id, @RequestBody Venue venue) {
        if (venue.getName() == null || venue.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Venue name is required");
        }
        if (venue.getCity() == null || venue.getCity().isEmpty()) {
            return ResponseEntity.badRequest().body("City is required");
        }
        if (venue.getCountry() == null || venue.getCountry().isEmpty()) {
            return ResponseEntity.badRequest().body("Country is required");
        }
        if (venue.getCapacity() == null || venue.getCapacity() <= 0) {
            return ResponseEntity.badRequest().body("Capacity must be positive");
        }
        
        return venueRepository.findById(id)
                .map(existingVenue -> {
                    venue.setId(id);
                    Venue updatedVenue = venueService.updateVenue(venue);
                    return ResponseEntity.ok(updatedVenue);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteVenue(@PathVariable Long id) {
        try {
            if (!venueRepository.existsById(id)) {
                return new ResponseEntity<>("Venue not found", HttpStatus.NOT_FOUND);
            }
            venueService.deleteVenue(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting venue: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}