package com.app.service;

import com.app.exception.ResourceNotFoundException;
import com.app.model.Show;
import com.app.repository.ShowRepository;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShowService {

    private final ShowRepository showRepository;

    public ShowService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }
    public List<Show> getAllShows() {
        List<Show> shows = showRepository.findAll();
        if (shows.isEmpty()) {
            throw new ResourceNotFoundException("No shows available at the moment.");
        }
        return shows;
    }

    // ✅ **Search shows by title (case-insensitive)**
    public List<Show> searchShows(String title) {
        return showRepository.findByTitleContainingIgnoreCase(title);
    }

    // ✅ **Filter shows by genre**
    public List<Show> filterShowsByGenre(String genre) {
        return showRepository.findByGenre(genre);
    }

    // ✅ **Filter shows by date range**
    public List<Show> filterShowsByDate(LocalDateTime start, LocalDateTime end) {
        return showRepository.findByShowTimeBetween(start, end);
    }

    // ✅ **Filter shows by location**
    public List<Show> filterShowsByLocation(String location) {
        return showRepository.findByVenueContainingIgnoreCase(location);
    }

    // ✅ **Get show by ID**
    public Show getShowById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
    }

    // ✅ **Create a new show**
    @Secured("ROLE_ADMIN") 
    public Show createShow(Show show) {
        if (show.getTitle() == null || show.getTitle().isEmpty() ||
            show.getGenre() == null  ||
            show.getShowTime() == null ||
            show.getVenue() == null || show.getVenue().isEmpty() ||
            show.getPrice() <= 0) {
            throw new IllegalArgumentException("Invalid show details. Please check all fields.");
        }
        return showRepository.save(show);
    }

    // ✅ **Update an existing show**
    @Transactional
    public Show updateShow(Long id, Show updatedShow) {
        Show existingShow = getShowById(id);

        existingShow.setTitle(updatedShow.getTitle());
        existingShow.setGenre(updatedShow.getGenre());
        existingShow.setShowTime(updatedShow.getShowTime());
        existingShow.setPrice(updatedShow.getPrice());
        existingShow.setVenue(updatedShow.getVenue());

        return showRepository.save(existingShow);
    }

    // ✅ **Delete a show**
    @Transactional
    public boolean deleteShow(Long id) {
        if (showRepository.existsById(id)) {  // ✅ Check if show exists
            showRepository.deleteById(id);
            return true;  // ✅ Return a boolean
        } else {
            throw new ResourceNotFoundException("Show not found with ID: " + id);
        }
    }

}
