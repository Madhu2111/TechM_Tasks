package com.showvault.service;

import com.showvault.model.Show;
import com.showvault.models.User;
import com.showvault.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

    private final ShowRepository showRepository;

    @Autowired
    public ShowService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    @Transactional(readOnly = true)
    public List<Show> getAllShows() {
        try {
            List<Show> shows = showRepository.findAll();
            initializeShowCollections(shows);
            return shows;
        } catch (Exception e) {
            System.err.println("Error in getAllShows: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Show> getShowById(Long id) {
        Optional<Show> showOpt = showRepository.findById(id);
        
        // Initialize lazy-loaded collections if show is present
        if (showOpt.isPresent()) {
            Show show = showOpt.get();
            
            // Initialize createdBy
            if (show.getCreatedBy() != null) {
                show.getCreatedBy().getUsername(); // Force initialization
            }
            
            // Initialize schedules
            if (show.getSchedules() != null) {
                show.getSchedules().size(); // This will initialize the collection
            }
        }
        
        return showOpt;
    }

    @Transactional(readOnly = true)
    public List<Show> getShowsByStatus(Show.ShowStatus status) {
        List<Show> shows = showRepository.findByStatus(status);
        initializeShowCollections(shows);
        return shows;
    }

    @Transactional(readOnly = true)
    public List<Show> getShowsByGenre(String genre) {
        List<Show> shows = showRepository.findByGenre(genre);
        initializeShowCollections(shows);
        return shows;
    }

    @Transactional(readOnly = true)
    public List<Show> getShowsByLanguage(String language) {
        List<Show> shows = showRepository.findByLanguage(language);
        initializeShowCollections(shows);
        return shows;
    }

    @Transactional(readOnly = true)
    public List<Show> getShowsByCreator(User creator) {
        List<Show> shows = showRepository.findByCreatedById(creator.getId());
        initializeShowCollections(shows);
        return shows;
    }
    
    // Helper method to initialize lazy-loaded collections
    private void initializeShowCollections(List<Show> shows) {
        for (Show show : shows) {
            // Initialize createdBy
            if (show.getCreatedBy() != null) {
                show.getCreatedBy().getUsername(); // Force initialization
            }
            
            // Initialize schedules
            if (show.getSchedules() != null) {
                show.getSchedules().size(); // This will initialize the collection
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Show> searchShowsByTitle(String title) {
        try {
            List<Show> shows = showRepository.findByTitleContainingIgnoreCase(title);
            initializeShowCollections(shows);
            return shows;
        } catch (Exception e) {
            System.err.println("Error in searchShowsByTitle: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> getAllGenres() {
        return showRepository.findAllGenres();
    }

    public List<String> getAllLanguages() {
        return showRepository.findAllLanguages();
    }

    @Transactional
    public Show createShow(Show show) {
        return showRepository.save(show);
    }

    @Transactional
    public Show updateShow(Show show) {
        return showRepository.save(show);
    }

    @Transactional
    public void deleteShow(Long id) {
        showRepository.deleteById(id);
    }

    @Transactional
    public boolean updateShowStatus(Long showId, Show.ShowStatus newStatus) {
        Optional<Show> showOpt = showRepository.findById(showId);
        
        if (showOpt.isPresent()) {
            Show show = showOpt.get();
            show.setStatus(newStatus);
            showRepository.save(show);
            return true;
        }
        
        return false;
    }
}