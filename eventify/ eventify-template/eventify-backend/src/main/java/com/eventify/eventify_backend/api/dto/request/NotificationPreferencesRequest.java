package com.eventify.eventify_backend.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesRequest {


    private Boolean notifyNewEvents;

    private Boolean notifyUpcoming;

    @Min(value = 1, message = "Notification hours must be between 1 and 24")
    @Max(value = 24, message = "Notification hours must be between 1 and 24")
    private Integer notifyBeforeHours;
}