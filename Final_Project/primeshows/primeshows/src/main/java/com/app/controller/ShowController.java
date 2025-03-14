package com.app.controller;

import com.app.model.Show;
import com.app.service.ShowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<List<Show>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }
 

    @GetMapping("/{id}")
    public ResponseEntity<?> getShowById(@PathVariable Long id) {
        Show show = showService.getShowById(id);
        return (show != null) ? ResponseEntity.ok(show) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Show not found");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Show> createShow(@RequestBody Show show) {
        Show createdShow = showService.createShow(show);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShow);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShow(@PathVariable Long id, @RequestBody Show show) {
        Show updatedShow = showService.updateShow(id, show);
        return (updatedShow != null) ? ResponseEntity.ok(updatedShow) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Show not found");
    }
    
   

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShow(@PathVariable Long id) {
        boolean deleted = showService.deleteShow(id);
        
        
        return deleted ? ResponseEntity.ok("Show deleted successfully") : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Show not found");
    }
}

