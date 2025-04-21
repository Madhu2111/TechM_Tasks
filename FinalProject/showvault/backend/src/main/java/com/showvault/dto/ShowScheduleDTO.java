package com.showvault.dto;

import com.showvault.model.ShowSchedule;
import com.showvault.model.ShowSchedule.ScheduleStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ShowScheduleDTO {
    private Long id;
    private Long showId;
    private String showTitle;
    private Long venueId;
    private String venueName;
    private LocalDate showDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal basePrice;
    private ScheduleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer availableSeats;
    private Integer totalSeats;
    
    public ShowScheduleDTO(ShowSchedule schedule) {
        this.id = schedule.getId();
        this.showId = schedule.getShow() != null ? schedule.getShow().getId() : null;
        this.showTitle = schedule.getShow() != null ? schedule.getShow().getTitle() : null;
        this.venueId = schedule.getVenue() != null ? schedule.getVenue().getId() : null;
        this.venueName = schedule.getVenue() != null ? schedule.getVenue().getName() : null;
        this.showDate = schedule.getShowDate();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.basePrice = schedule.getBasePrice();
        this.status = schedule.getStatus();
        this.createdAt = schedule.getCreatedAt();
        this.updatedAt = schedule.getUpdatedAt();
        
        // We don't include bookings in the DTO to avoid circular references
    }
}