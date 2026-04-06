package com.eventify.eventify_backend.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesResponse {


    private Boolean notifyNewEvents;
    private Boolean notifyUpcoming;
    private Integer notifyBeforeHours;
}