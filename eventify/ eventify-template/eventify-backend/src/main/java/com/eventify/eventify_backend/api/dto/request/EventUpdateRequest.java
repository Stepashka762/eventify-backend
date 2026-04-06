package com.eventify.eventify_backend.api.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateRequest {

    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    private String description;

    @Future(message = "Event date must be in the future")
    private LocalDateTime dateTime;

    @Min(value = 1, message = "Total tickets must be at least 1")
    private Integer totalTickets;

    private String coverUrl;
}