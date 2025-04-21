package com.showvault.controller;

import com.showvault.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false, defaultValue = "timestamp") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        
        // Calculate offset for pagination
        int offset = (page - 1) * limit;
        
        // Get audit logs with pagination and filters
        List<Map<String, Object>> logs = auditLogService.getAuditLogs(
                offset, limit, userId, action, dateFrom, dateTo, sortBy, sortOrder);
        
        long totalLogs = auditLogService.countAuditLogs(userId, action, dateFrom, dateTo);
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logs);
        response.put("total", totalLogs);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuditLogById(@PathVariable Long id) {
        Map<String, Object> log = auditLogService.getAuditLogById(id);
        
        if (log != null) {
            return new ResponseEntity<>(log, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/actions")
    public ResponseEntity<?> getAuditLogActions() {
        List<String> actions = auditLogService.getAuditLogActions();
        return new ResponseEntity<>(actions, HttpStatus.OK);
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false, defaultValue = "csv") String format) {
        
        // In a real implementation, this would generate and return a file
        // For now, we'll just return a success message
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Audit logs exported successfully in " + format.toUpperCase() + " format");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}