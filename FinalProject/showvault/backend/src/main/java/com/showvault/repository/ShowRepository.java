package com.showvault.repository;

import com.showvault.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    
    List<Show> findByStatus(Show.ShowStatus status);
    
    long countByStatus(Show.ShowStatus status);
    
    List<Show> findByGenre(String genre);
    
    List<Show> findByLanguage(String language);
    
    List<Show> findByCreatedById(Long userId);
    
    @Query("SELECT s FROM Show s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Show> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT DISTINCT s.genre FROM Show s WHERE s.genre IS NOT NULL")
    List<String> findAllGenres();
    
    @Query("SELECT DISTINCT s.language FROM Show s WHERE s.language IS NOT NULL")
    List<String> findAllLanguages();
    
    @Query(value = "SELECT s.* FROM shows s JOIN schedules ss ON s.id = ss.show_id JOIN bookings b ON ss.id = b.schedule_id " +
           "WHERE b.booking_date BETWEEN :startDate AND :endDate " +
           "GROUP BY s.id ORDER BY COUNT(b) DESC LIMIT :limit", nativeQuery = true)
    List<Show> findTrendingShowsByBookingCount(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate, 
                                             @Param("limit") int limit);
    
    @Query(value = "SELECT s.* FROM shows s JOIN schedules ss ON s.id = ss.show_id " +
           "JOIN bookings b ON ss.id = b.schedule_id " +
           "JOIN booking_payment p ON b.id = p.booking_id " +
           "WHERE p.status = 'COMPLETED' AND p.payment_date BETWEEN :startDate AND :endDate " +
           "GROUP BY s.id ORDER BY SUM(p.amount) DESC LIMIT :limit", nativeQuery = true)
    List<Show> findTopShowsByRevenue(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate, 
                                    @Param("limit") int limit);
    
    @Query("SELECT COUNT(DISTINCT b.user.id) FROM Show s JOIN s.schedules ss JOIN ss.bookings b " +
           "WHERE s.id = :showId AND b.status = 'CONFIRMED'")
    Long countUniqueViewersByShowId(@Param("showId") Long showId);
}