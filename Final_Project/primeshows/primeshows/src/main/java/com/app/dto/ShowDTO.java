package com.app.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShowDTO {

    private Long id; // Useful for updating shows

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Genre cannot be empty")
    private String genre;

    @NotBlank(message = "Language cannot be empty")
    private String language; // Example: English, Hindi, etc.

    @Future(message = "Show time must be in the future")
    @NotNull(message = "Show time cannot be empty")
    private LocalDateTime showTime;

    @Min(value = 0, message = "Price must be a positive value")
    private double price;

    @NotBlank(message = "Venue cannot be empty")
    private String venue;

    @NotNull(message = "Organizer ID is required")
    private Long organizerId; // To associate the show with an organizer
}
