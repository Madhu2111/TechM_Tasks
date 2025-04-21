package com.showvault.service;

import com.showvault.model.AuditLog;
import com.showvault.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AuditLogService {
    
    AuditLog createLog(AuditLog log);
    
    AuditLog createLog(User user, String action, AuditLog.EntityType entityType);
    
    AuditLog createLog(User user, String action, AuditLog.EntityType entityType, Long entityId);
    
    AuditLog createLog(User user, String action, AuditLog.EntityType entityType, Long entityId, String details);
    
    Optional<AuditLog> getLogById(Long id);
    
    List<AuditLog> getAllLogs();
    
    Page<AuditLog> getAllLogs(Pageable pageable);
    
    List<AuditLog> getLogsByUser(User user);
    
    Page<AuditLog> getLogsByUser(User user, Pageable pageable);
    
    List<AuditLog> getLogsByAction(String action);
    
    Page<AuditLog> getLogsByAction(String action, Pageable pageable);
    
    List<AuditLog> getLogsByEntityType(AuditLog.EntityType entityType);
    
    Page<AuditLog> getLogsByEntityType(AuditLog.EntityType entityType, Pageable pageable);
    
    List<AuditLog> getLogsByEntity(AuditLog.EntityType entityType, Long entityId);
    
    Page<AuditLog> getLogsByEntity(AuditLog.EntityType entityType, Long entityId, Pageable pageable);
    
    List<AuditLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    Page<AuditLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    List<AuditLog> getLogsByUserAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    Page<AuditLog> getLogsByUserAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    List<AuditLog> searchLogsByAction(String actionKeyword);
    
    Page<AuditLog> searchLogsByAction(String actionKeyword, Pageable pageable);
    
    List<AuditLog> searchLogsByDetails(String detailsKeyword);
    
    Page<AuditLog> searchLogsByDetails(String detailsKeyword, Pageable pageable);
    
    /**
     * Get audit logs with pagination and filters for admin dashboard
     * @param offset Pagination offset
     * @param limit Pagination limit
     * @param userId User ID filter
     * @param action Action filter
     * @param dateFrom Start date filter
     * @param dateTo End date filter
     * @param sortBy Field to sort by
     * @param sortOrder Sort order (asc/desc)
     * @return List of audit log entries
     */
    List<Map<String, Object>> getAuditLogs(
            int offset, int limit, Long userId, String action, 
            LocalDate dateFrom, LocalDate dateTo, String sortBy, String sortOrder);
    
    /**
     * Count audit logs with filters for admin dashboard
     * @param userId User ID filter
     * @param action Action filter
     * @param dateFrom Start date filter
     * @param dateTo End date filter
     * @return Total count of matching logs
     */
    long countAuditLogs(Long userId, String action, LocalDate dateFrom, LocalDate dateTo);
    
    /**
     * Get audit log by ID for admin dashboard
     * @param id Audit log ID
     * @return Audit log entry or null if not found
     */
    Map<String, Object> getAuditLogById(Long id);
    
    /**
     * Get list of all possible audit log actions for admin dashboard
     * @return List of action names
     */
    List<String> getAuditLogActions();
}