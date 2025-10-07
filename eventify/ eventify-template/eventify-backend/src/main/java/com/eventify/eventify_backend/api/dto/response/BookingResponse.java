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
public class BookingResponse {

    private Long id;
    private EventResponse event;
    private String customerEmail;
    private Integer ticketCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;
    private Boolean confirmed;
    private String timezone;
    private String status;
}