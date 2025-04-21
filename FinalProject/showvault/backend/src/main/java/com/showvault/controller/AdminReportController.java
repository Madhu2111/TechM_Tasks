package com.showvault.controller;

import com.showvault.service.AdminReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    @Autowired
    private AdminReportService adminReportService;

    @GetMapping("/sales")
    public ResponseEntity<?> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Map<String, Object> report;
        if (startDate != null && endDate != null) {
            report = adminReportService.getSalesReport(startDate, endDate);
        } else {
            report = adminReportService.getSalesReport();
        }
        
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUserReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Map<String, Object> report;
        if (startDate != null && endDate != null) {
            report = adminReportService.getUserReport(startDate, endDate);
        } else {
            report = adminReportService.getUserReport();
        }
        
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getBookingReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Map<String, Object> report;
        if (startDate != null && endDate != null) {
            report = adminReportService.getBookingReport(startDate, endDate);
        } else {
            report = adminReportService.getBookingReport();
        }
        
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "monthly") String interval) {
        
        Map<String, Object> report;
        if (startDate != null && endDate != null) {
            report = adminReportService.getRevenueReport(startDate, endDate, interval);
        } else {
            report = adminReportService.getRevenueReport(interval);
        }
        
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/export/{reportType}")
    public ResponseEntity<?> exportReport(
            @PathVariable String reportType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "pdf") String format) {
        
        // In a real implementation, this would generate and return a file
        // For now, we'll just return a success message
        
        return new ResponseEntity<>(Map.of(
            "success", true,
            "message", reportType + " report exported successfully in " + format.toUpperCase() + " format"
        ), HttpStatus.OK);
    }
}