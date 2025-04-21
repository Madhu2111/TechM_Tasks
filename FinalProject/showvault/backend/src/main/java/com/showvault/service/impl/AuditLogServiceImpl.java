package com.showvault.service.impl;

import com.showvault.model.AuditLog;
import com.showvault.models.User;
import com.showvault.repository.AuditLogRepository;
import com.showvault.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public AuditLog createLog(AuditLog log) {
        return auditLogRepository.save(log);
    }

    @Override
    @Transactional
    public AuditLog createLog(User user, String action, AuditLog.EntityType entityType) {
        AuditLog log = new AuditLog(user, action, entityType);
        return auditLogRepository.save(log);
    }

    @Override
    @Transactional
    public AuditLog createLog(User user, String action, AuditLog.EntityType entityType, Long entityId) {
        AuditLog log = new AuditLog(user, action, entityType, entityId);
        return auditLogRepository.save(log);
    }

    @Override
    @Transactional
    public AuditLog createLog(User user, String action, AuditLog.EntityType entityType, Long entityId, String details) {
        AuditLog log = new AuditLog(user, action, entityType, entityId);
        log.setDetails(details);
        return auditLogRepository.save(log);
    }

    @Override
    public Optional<AuditLog> getLogById(Long id) {
        return auditLogRepository.findById(id);
    }

    @Override
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Override
    public List<AuditLog> getLogsByUser(User user) {
        return auditLogRepository.findByUserOrderByTimestampDesc(user);
    }

    @Override
    public Page<AuditLog> getLogsByUser(User user, Pageable pageable) {
        return auditLogRepository.findByUserOrderByTimestampDesc(user, pageable);
    }

    @Override
    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action);
    }

    @Override
    public Page<AuditLog> getLogsByAction(String action, Pageable pageable) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action, pageable);
    }

    @Override
    public List<AuditLog> getLogsByEntityType(AuditLog.EntityType entityType) {
        return auditLogRepository.findByEntityTypeOrderByTimestampDesc(entityType);
    }

    @Override
    public Page<AuditLog> getLogsByEntityType(AuditLog.EntityType entityType, Pageable pageable) {
        return auditLogRepository.findByEntityTypeOrderByTimestampDesc(entityType, pageable);
    }

    @Override
    public List<AuditLog> getLogsByEntity(AuditLog.EntityType entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    @Override
    public Page<AuditLog> getLogsByEntity(AuditLog.EntityType entityType, Long entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId, pageable);
    }

    @Override
    public List<AuditLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(startTime, endTime);
    }

    @Override
    public Page<AuditLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(startTime, endTime, pageable);
    }

    @Override
    public List<AuditLog> getLogsByUserAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.findByUserAndTimestampBetween(userId, startTime, endTime);
    }

    @Override
    public Page<AuditLog> getLogsByUserAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return auditLogRepository.findByUserAndTimestampBetween(userId, startTime, endTime, pageable);
    }

    @Override
    public List<AuditLog> searchLogsByAction(String actionKeyword) {
        return auditLogRepository.findByActionContaining(actionKeyword);
    }

    @Override
    public Page<AuditLog> searchLogsByAction(String actionKeyword, Pageable pageable) {
        return auditLogRepository.findByActionContaining(actionKeyword, pageable);
    }

    @Override
    public List<AuditLog> searchLogsByDetails(String detailsKeyword) {
        return auditLogRepository.findByDetailsContaining(detailsKeyword);
    }

    @Override
    public Page<AuditLog> searchLogsByDetails(String detailsKeyword, Pageable pageable) {
        return auditLogRepository.findByDetailsContaining(detailsKeyword, pageable);
    }
    
    @Override
    public List<Map<String, Object>> getAuditLogs(int offset, int limit, Long userId, String action, 
                                                 LocalDate dateFrom, LocalDate dateTo, String sortBy, String sortOrder) {
        // Create pageable with sorting
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, 
                           sortBy != null ? sortBy : "timestamp");
        
        Pageable pageable = PageRequest.of(offset / limit, limit, sort);
        
        // Convert LocalDate to LocalDateTime for filtering
        LocalDateTime startDateTime = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime endDateTime = dateTo != null ? dateTo.atTime(LocalTime.MAX) : null;
        
        // Get logs with filters
        Page<AuditLog> logsPage;
        
        if (userId != null && action != null && startDateTime != null && endDateTime != null) {
            logsPage = auditLogRepository.findByUserIdAndActionAndTimestampBetween(userId, action, startDateTime, endDateTime, pageable);
        } else if (userId != null && action != null) {
            logsPage = auditLogRepository.findByUserIdAndAction(userId, action, pageable);
        } else if (userId != null && startDateTime != null && endDateTime != null) {
            logsPage = auditLogRepository.findByUserAndTimestampBetween(userId, startDateTime, endDateTime, pageable);
        } else if (action != null && startDateTime != null && endDateTime != null) {
            logsPage = auditLogRepository.findByActionAndTimestampBetween(action, startDateTime, endDateTime, pageable);
        } else if (userId != null) {
            logsPage = auditLogRepository.findByUserId(userId, pageable);
        } else if (action != null) {
            logsPage = auditLogRepository.findByActionOrderByTimestampDesc(action, pageable);
        } else if (startDateTime != null && endDateTime != null) {
            logsPage = auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(startDateTime, endDateTime, pageable);
        } else {
            logsPage = auditLogRepository.findAll(pageable);
        }
        
        // Convert to list of maps for the response
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (AuditLog log : logsPage.getContent()) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("id", log.getId());
            logMap.put("userId", log.getUser() != null ? log.getUser().getId() : null);
            logMap.put("username", log.getUser() != null ? log.getUser().getUsername() : null);
            logMap.put("action", log.getAction());
            logMap.put("entityType", log.getEntityType() != null ? log.getEntityType().toString() : null);
            logMap.put("entityId", log.getEntityId());
            logMap.put("details", log.getDetails());
            logMap.put("timestamp", log.getTimestamp());
            logMap.put("ipAddress", log.getIpAddress());
            
            result.add(logMap);
        }
        
        return result;
    }

    @Override
    public long countAuditLogs(Long userId, String action, LocalDate dateFrom, LocalDate dateTo) {
        // Convert LocalDate to LocalDateTime for filtering
        LocalDateTime startDateTime = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime endDateTime = dateTo != null ? dateTo.atTime(LocalTime.MAX) : null;
        
        // Count logs with filters
        if (userId != null && action != null && startDateTime != null && endDateTime != null) {
            return auditLogRepository.countByUserIdAndActionAndTimestampBetween(userId, action, startDateTime, endDateTime);
        } else if (userId != null && action != null) {
            return auditLogRepository.countByUserIdAndAction(userId, action);
        } else if (userId != null && startDateTime != null && endDateTime != null) {
            return auditLogRepository.countByUserIdAndTimestampBetween(userId, startDateTime, endDateTime);
        } else if (action != null && startDateTime != null && endDateTime != null) {
            return auditLogRepository.countByActionAndTimestampBetween(action, startDateTime, endDateTime);
        } else if (userId != null) {
            return auditLogRepository.countByUserId(userId);
        } else if (action != null) {
            return auditLogRepository.countByAction(action);
        } else if (startDateTime != null && endDateTime != null) {
            return auditLogRepository.countByTimestampBetween(startDateTime, endDateTime);
        } else {
            return auditLogRepository.count();
        }
    }

    @Override
    public Map<String, Object> getAuditLogById(Long id) {
        Optional<AuditLog> logOpt = auditLogRepository.findById(id);
        
        if (logOpt.isPresent()) {
            AuditLog log = logOpt.get();
            
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("id", log.getId());
            logMap.put("userId", log.getUser() != null ? log.getUser().getId() : null);
            logMap.put("username", log.getUser() != null ? log.getUser().getUsername() : null);
            logMap.put("action", log.getAction());
            logMap.put("entityType", log.getEntityType() != null ? log.getEntityType().toString() : null);
            logMap.put("entityId", log.getEntityId());
            logMap.put("details", log.getDetails());
            logMap.put("timestamp", log.getTimestamp());
            logMap.put("ipAddress", log.getIpAddress());
            
            return logMap;
        }
        
        return null;
    }

    @Override
    public List<String> getAuditLogActions() {
        // In a real implementation, this would query distinct actions from the database
        // For now, we'll return a predefined list
        return Arrays.asList(
            "LOGIN",
            "LOGOUT",
            "USER_CREATE",
            "USER_UPDATE",
            "USER_DELETE",
            "USER_ROLE_UPDATE",
            "USER_SUSPEND",
            "BOOKING_CREATE",
            "BOOKING_UPDATE",
            "BOOKING_CANCEL",
            "BOOKING_REFUND",
            "SHOW_CREATE",
            "SHOW_UPDATE",
            "SHOW_DELETE",
            "VENUE_CREATE",
            "VENUE_UPDATE",
            "VENUE_DELETE",
            "SETTINGS_UPDATE",
            "SYSTEM_BACKUP"
        );
    }
}