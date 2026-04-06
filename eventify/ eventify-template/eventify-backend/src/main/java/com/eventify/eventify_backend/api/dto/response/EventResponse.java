package com.eventify.eventify_backend.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {


    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private Integer totalTickets;
    private Integer availableTickets;
    private String coverUrl;
}