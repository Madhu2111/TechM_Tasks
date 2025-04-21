package com.showvault.controller;

import com.showvault.dto.ShowScheduleDTO;
import com.showvault.model.Show;
import com.showvault.model.ShowSchedule;
import com.showvault.model.Venue;
import com.showvault.security.services.UserDetailsImpl;
import com.showvault.service.DTOConverterService;
import com.showvault.service.ShowScheduleService;
import com.showvault.service.ShowService;
import com.showvault.service.VenueService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/schedules")
public class ShowScheduleController {

    @Autowired
    private ShowScheduleService showScheduleService;

    @Autowired
    private ShowService showService;

    @Autowired
    private VenueService venueService;
    
    @Autowired
    private DTOConverterService dtoConverterService;

    @GetMapping
    public ResponseEntity<List<ShowScheduleDTO>> getAllSchedules() {
        List<ShowSchedule> schedules = showScheduleService.getAllShowSchedules();
        List<ShowScheduleDTO> scheduleDTOs = dtoConverterService.convertShowSchedulesToDTO(schedules);
        return new ResponseEntity<>(scheduleDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowSchedule> getScheduleById(@PathVariable Long id) {
        return showScheduleService.getShowScheduleById(id)
                .map(schedule -> new ResponseEntity<>(schedule, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/show/{showId}")
    public ResponseEntity<List<ShowSchedule>> getSchedulesByShowId(@PathVariable Long showId) {
        List<ShowSchedule> schedules = showScheduleService.getShowSchedulesByShowId(showId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<ShowSchedule>> getSchedulesByVenueId(@PathVariable Long venueId) {
        List<ShowSchedule> schedules = showScheduleService.getShowSchedulesByVenueId(venueId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<ShowSchedule>> getSchedulesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ShowSchedule> schedules = showScheduleService.getShowSchedulesByDate(date);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShowSchedule>> getSchedulesByStatus(@PathVariable ShowSchedule.ScheduleStatus status) {
        List<ShowSchedule> schedules = showScheduleService.getShowSchedulesByStatus(status);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<ShowSchedule>> getUpcomingSchedules() {
        List<ShowSchedule> schedules = showScheduleService.getUpcomingShowSchedules();
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ShowSchedule>> searchSchedules(
            @RequestParam(required = false) Long showId,
            @RequestParam(required = false) Long venueId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) ShowSchedule.ScheduleStatus status) {
        
        List<ShowSchedule> schedules = showScheduleService.searchShowSchedules(showId, venueId, fromDate, toDate, status);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<?> createSchedule(@RequestBody ShowSchedule showSchedule) {
        // Validate show exists and belongs to the organizer
        Optional<Show> showOpt = showService.getShowById(showSchedule.getShow().getId());
        if (!showOpt.isPresent()) {
            return new ResponseEntity<>("Show not found", HttpStatus.NOT_FOUND);
        }
        
        Show show = showOpt.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!show.getCreatedBy().getId().equals(userDetails.getId()) && 
                !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return new ResponseEntity<>("Not authorized to create schedule for this show", HttpStatus.FORBIDDEN);
        }
        
        // Validate venue exists
        Optional<Venue> venueOpt = venueService.getVenueById(showSchedule.getVenue().getId());
        if (!venueOpt.isPresent()) {
            return new ResponseEntity<>("Venue not found", HttpStatus.NOT_FOUND);
        }
        
        // Set default status for new schedule
        showSchedule.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
        
        // Create the schedule
        ShowSchedule newSchedule = showScheduleService.createShowSchedule(showSchedule);
        return new ResponseEntity<>(newSchedule, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody ShowSchedule showSchedule) {
        return showScheduleService.getShowScheduleById(id)
                .map(existingSchedule -> {
                    // Check if the user is the creator of the show or an admin
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    
                    if (existingSchedule.getShow().getCreatedBy().getId().equals(userDetails.getId()) || 
                            authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        
                        showSchedule.setId(id);
                        ShowSchedule updatedSchedule = showScheduleService.updateShowSchedule(showSchedule);
                        return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Not authorized to update this schedule", HttpStatus.FORBIDDEN);
                    }
                })
                .orElse(new ResponseEntity<>("Schedule not found", HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateScheduleStatus(
            @PathVariable Long id, 
            @RequestParam ShowSchedule.ScheduleStatus status) {
        
        return showScheduleService.getShowScheduleById(id)
                .map(existingSchedule -> {
                    // Check if the user is the creator of the show or an admin
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    
                    if (existingSchedule.getShow().getCreatedBy().getId().equals(userDetails.getId()) || 
                            authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        
                        boolean updated = showScheduleService.updateShowScheduleStatus(id, status);
                        return updated ? 
                                ResponseEntity.ok().build() : 
                                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        return showScheduleService.getShowScheduleById(id)
                .map(existingSchedule -> {
                    // Check if the user is the creator of the show or an admin
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    
                    if (existingSchedule.getShow().getCreatedBy().getId().equals(userDetails.getId()) || 
                            authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        
                        try {
                            showScheduleService.deleteShowSchedule(id);
                            return ResponseEntity.noContent().build();
                        } catch (Exception e) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}