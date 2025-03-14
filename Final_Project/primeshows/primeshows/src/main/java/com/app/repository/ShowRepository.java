package com.app.repository;

import com.app.model.Show;
import com.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    
    List<Show> findByTitleContainingIgnoreCase(String title);  // Search by Show name

    List<Show> findByGenre(String genre);  // Filter by genre

    List<Show> findByGenreIn(List<String> genres);  // Filter by multiple genres

    List<Show> findByLanguageIgnoreCase(String language);  // Filter by language

    List<Show> findByShowTimeBetween(LocalDateTime start, LocalDateTime end);  // Filter by date range

    List<Show> findByShowTimeAfter(LocalDateTime now);  // Find upcoming shows

    List<Show> findByVenueContainingIgnoreCase(String venue);  // Filter by location

    List<Show> findByOrganizer(User organizer);  // Find shows created by a specific organizer
}
