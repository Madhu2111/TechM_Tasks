package com.showvault.controller;

import com.showvault.model.AuditLog;
import com.showvault.models.User;
import com.showvault.service.AuditLogService;
import com.showvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<AuditLog> logsPage = auditLogService.getAllLogs(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logsPage.getContent());
        response.put("currentPage", logsPage.getNumber());
        response.put("totalItems", logsPage.getTotalElements());
        response.put("totalPages", logsPage.getTotalPages());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getLogById(@PathVariable Long id) {
        Optional<AuditLog> logOpt = auditLogService.getLogById(id);
        
        if (logOpt.isPresent()) {
            return new ResponseEntity<>(logOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getLogsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Optional<User> userOpt = userService.getUserById(userId);
        
        if (userOpt.isPresent()) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
            Page<AuditLog> logsPage = auditLogService.getLogsByUser(userOpt.get(), pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("logs", logsPage.getContent());
            response.put("currentPage", logsPage.getNumber());
            response.put("totalItems", logsPage.getTotalElements());
            response.put("totalPages", logsPage.getTotalPages());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<Map<String, Object>> getLogsByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logsPage = auditLogService.getLogsByAction(action, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logsPage.getContent());
        response.put("currentPage", logsPage.getNumber());
        response.put("totalItems", logsPage.getTotalElements());
        response.put("totalPages", logsPage.getTotalPages());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/entity-type/{entityType}")
    public ResponseEntity<Map<String, Object>> getLogsByEntityType(
            @PathVariable AuditLog.EntityType entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logsPage = auditLogService.getLogsByEntityType(entityType, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logsPage.getContent());
        response.put("currentPage", logsPage.getNumber());
        response.put("totalItems", logsPage.getTotalElements());
        response.put("totalPages", logsPage.getTotalPages());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<Map<String, Object>> getLogsByEntity(
            @PathVariable AuditLog.EntityType entityType,
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logsPage = auditLogService.getLogsByEntity(entityType, entityId, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logsPage.getContent());
        response.put("currentPage", logsPage.getNumber());
        response.put("totalItems", logsPage.getTotalElements());
        response.put("totalPages", logsPage.getTotalPages());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/time-range")
    public ResponseEntity<Map<String, Object>> getLogsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logsPage = auditLogService.getLogsByTimeRange(startTime, endTime, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logsPage.getContent());
        response.put("currentPage", logsPage.getNumber());
        response.put("totalItems", logsPage.getTotalElements());
        response.put("totalPages", logsPage.getTotalPages());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/time-range")
    public ResponseEntity<Map<String, Object>> getLogsByUserAndTimeRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logsPage = auditLogService.getLogsByUserAndTimeRange(userId, startTime, endTime, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logsPage.getContent());
        response.put("currentPage", logsPage.getNumber());
        response.put("totalItems", logsPage.getTotalElements());
        response.put("totalPages", logsPage.getTotalPages());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search/action")
    public ResponseEntity<Map<String, Object>> searchLogsByAction(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logsPage = auditLogService.searchLogsByAction(keyword, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logsPage.getContent());
        response.put("currentPage", logsPage.getNumber());
        response.put("totalItems", logsPage.getTotalElements());
        response.put("totalPages", logsPage.getTotalPages());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search/details")
    public ResponseEntity<Map<String, Object>> searchLogsByDetails(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logsPage = auditLogService.searchLogsByDetails(keyword, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logsPage.getContent());
        response.put("currentPage", logsPage.getNumber());
        response.put("totalItems", logsPage.getTotalElements());
        response.put("totalPages", logsPage.getTotalPages());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}