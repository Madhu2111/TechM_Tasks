package com.showvault.controller;

import com.showvault.dto.ShowDTO;
import com.showvault.model.Show;
import com.showvault.models.User;
import com.showvault.security.services.UserDetailsImpl;
import com.showvault.service.DTOConverterService;
import com.showvault.service.ShowService;
import com.showvault.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private DTOConverterService dtoConverterService;

    @GetMapping
    public ResponseEntity<List<ShowDTO>> getAllShows() {
        try {
            List<Show> shows = showService.getAllShows();
            List<ShowDTO> showDTOs = dtoConverterService.convertShowsToDTO(shows);
            return new ResponseEntity<>(showDTOs, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching all shows: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowDTO> getShowById(@PathVariable Long id) {
        return showService.getShowById(id)
                .map(show -> new ResponseEntity<>(dtoConverterService.convertToShowDTO(show), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShowDTO>> getShowsByStatus(@PathVariable Show.ShowStatus status) {
        List<Show> shows = showService.getShowsByStatus(status);
        List<ShowDTO> showDTOs = dtoConverterService.convertShowsToDTO(shows);
        return new ResponseEntity<>(showDTOs, HttpStatus.OK);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<ShowDTO>> getShowsByGenre(@PathVariable String genre) {
        List<Show> shows = showService.getShowsByGenre(genre);
        List<ShowDTO> showDTOs = dtoConverterService.convertShowsToDTO(shows);
        return new ResponseEntity<>(showDTOs, HttpStatus.OK);
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<ShowDTO>> getShowsByLanguage(@PathVariable String language) {
        List<Show> shows = showService.getShowsByLanguage(language);
        List<ShowDTO> showDTOs = dtoConverterService.convertShowsToDTO(shows);
        return new ResponseEntity<>(showDTOs, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ShowDTO>> searchShowsByTitle(@RequestParam String title) {
        List<Show> shows = showService.searchShowsByTitle(title);
        List<ShowDTO> showDTOs = dtoConverterService.convertShowsToDTO(shows);
        return new ResponseEntity<>(showDTOs, HttpStatus.OK);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<List<ShowDTO>> filterShows(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String venue,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer pageSize) {
        
        try {
            System.out.println("Filtering shows with parameters: type=" + type + ", search=" + search + 
                ", sort=" + sort + ", page=" + page + ", pageSize=" + pageSize);
            
            // Create a list to store the filtered shows
            List<Show> shows = new ArrayList<>();
            
            // Get all shows first
            try {
                shows = showService.getAllShows();
                System.out.println("Retrieved " + shows.size() + " shows from database");
            } catch (Exception e) {
                System.err.println("Error retrieving all shows: " + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            
            // Apply basic filtering if search is provided
            if (search != null && !search.isEmpty()) {
                try {
                    shows = showService.searchShowsByTitle(search);
                    System.out.println("Filtered by search term '" + search + "', found " + shows.size() + " shows");
                } catch (Exception e) {
                    System.err.println("Error searching shows by title: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Apply type/category filtering if provided
            if (type != null && !type.isEmpty() && !type.equalsIgnoreCase("all")) {
                try {
                    final String typeFilter = type;
                    shows = shows.stream()
                        .filter(show -> typeFilter.equalsIgnoreCase(show.getType()))
                        .collect(Collectors.toList());
                    System.out.println("Filtered by type '" + type + "', found " + shows.size() + " shows");
                } catch (Exception e) {
                    System.err.println("Error filtering shows by type: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Convert to DTOs and return the filtered shows
            List<ShowDTO> showDTOs = dtoConverterService.convertShowsToDTO(shows);
            return new ResponseEntity<>(showDTOs, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error filtering shows: " + e.getMessage());
            e.printStackTrace();
            // Return an empty list instead of an error to avoid breaking the frontend
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
    }

    @GetMapping("/genres")
    public ResponseEntity<List<String>> getAllGenres() {
        List<String> genres = showService.getAllGenres();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @GetMapping("/languages")
    public ResponseEntity<List<String>> getAllLanguages() {
        List<String> languages = showService.getAllLanguages();
        return new ResponseEntity<>(languages, HttpStatus.OK);
    }

    @GetMapping("/my-shows")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<ShowDTO>> getMyShows() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userService.getUserById(userDetails.getId());
        if (userOpt.isPresent()) {
            List<Show> shows = showService.getShowsByCreator(userOpt.get());
            List<ShowDTO> showDTOs = dtoConverterService.convertShowsToDTO(shows);
            return new ResponseEntity<>(showDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Show> createShow(@RequestBody Show show) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userService.getUserById(userDetails.getId());
        if (userOpt.isPresent()) {
            show.setCreatedBy(userOpt.get());
            show.setStatus(Show.ShowStatus.UPCOMING); // Default status for new shows
            Show newShow = showService.createShow(show);
            return new ResponseEntity<>(newShow, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateShow(@PathVariable Long id, @RequestBody Show show) {
        return showService.getShowById(id)
                .map(existingShow -> {
                    // Check if the user is the creator or an admin
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    
                    if (existingShow.getCreatedBy().getId().equals(userDetails.getId()) || 
                            authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        
                        show.setId(id);
                        show.setCreatedBy(existingShow.getCreatedBy());
                        Show updatedShow = showService.updateShow(show);
                        return ResponseEntity.ok(updatedShow);
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateShowStatus(
            @PathVariable Long id, 
            @RequestParam Show.ShowStatus status) {
        
        return showService.getShowById(id)
                .map(existingShow -> {
                    // Check if the user is the creator or an admin
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    
                    if (existingShow.getCreatedBy().getId().equals(userDetails.getId()) || 
                            authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        
                        boolean updated = showService.updateShowStatus(id, status);
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
    public ResponseEntity<?> deleteShow(@PathVariable Long id) {
        return showService.getShowById(id)
                .map(existingShow -> {
                    // Check if the user is the creator or an admin
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    
                    if (existingShow.getCreatedBy().getId().equals(userDetails.getId()) || 
                            authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        
                        try {
                            showService.deleteShow(id);
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