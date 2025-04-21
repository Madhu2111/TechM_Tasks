package com.showvault.repository;

import com.showvault.model.AuditLog;
import com.showvault.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUserOrderByTimestampDesc(User user);
    
    Page<AuditLog> findByUserOrderByTimestampDesc(User user, Pageable pageable);
    
    List<AuditLog> findByActionOrderByTimestampDesc(String action);
    
    Page<AuditLog> findByActionOrderByTimestampDesc(String action, Pageable pageable);
    
    List<AuditLog> findByEntityTypeOrderByTimestampDesc(AuditLog.EntityType entityType);
    
    Page<AuditLog> findByEntityTypeOrderByTimestampDesc(AuditLog.EntityType entityType, Pageable pageable);
    
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(AuditLog.EntityType entityType, Long entityId);
    
    Page<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(AuditLog.EntityType entityType, Long entityId, Pageable pageable);
    
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    Page<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = ?1 AND a.timestamp BETWEEN ?2 AND ?3 ORDER BY a.timestamp DESC")
    List<AuditLog> findByUserAndTimestampBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = ?1 AND a.timestamp BETWEEN ?2 AND ?3 ORDER BY a.timestamp DESC")
    Page<AuditLog> findByUserAndTimestampBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.action LIKE %?1% ORDER BY a.timestamp DESC")
    List<AuditLog> findByActionContaining(String actionKeyword);
    
    @Query("SELECT a FROM AuditLog a WHERE a.action LIKE %?1% ORDER BY a.timestamp DESC")
    Page<AuditLog> findByActionContaining(String actionKeyword, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.details LIKE %?1% ORDER BY a.timestamp DESC")
    List<AuditLog> findByDetailsContaining(String detailsKeyword);
    
    @Query("SELECT a FROM AuditLog a WHERE a.details LIKE %?1% ORDER BY a.timestamp DESC")
    Page<AuditLog> findByDetailsContaining(String detailsKeyword, Pageable pageable);
    
    // Additional methods for admin dashboard
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = ?1")
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = ?1 AND a.action = ?2")
    Page<AuditLog> findByUserIdAndAction(Long userId, String action, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = ?1 AND a.action = ?2 AND a.timestamp BETWEEN ?3 AND ?4")
    Page<AuditLog> findByUserIdAndActionAndTimestampBetween(Long userId, String action, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.action = ?1 AND a.timestamp BETWEEN ?2 AND ?3")
    Page<AuditLog> findByActionAndTimestampBetween(String action, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.user.id = ?1")
    long countByUserId(Long userId);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.action = ?1")
    long countByAction(String action);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.timestamp BETWEEN ?1 AND ?2")
    long countByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.user.id = ?1 AND a.action = ?2")
    long countByUserIdAndAction(Long userId, String action);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.user.id = ?1 AND a.timestamp BETWEEN ?2 AND ?3")
    long countByUserIdAndTimestampBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.action = ?1 AND a.timestamp BETWEEN ?2 AND ?3")
    long countByActionAndTimestampBetween(String action, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.user.id = ?1 AND a.action = ?2 AND a.timestamp BETWEEN ?3 AND ?4")
    long countByUserIdAndActionAndTimestampBetween(Long userId, String action, LocalDateTime startTime, LocalDateTime endTime);
}