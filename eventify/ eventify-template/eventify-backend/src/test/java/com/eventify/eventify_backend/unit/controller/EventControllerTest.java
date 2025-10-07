package com.eventify.eventify_backend.unit.controller;

import com.eventify.eventify_backend.api.dto.response.EventResponse;
import com.eventify.eventify_backend.api.dto.response.PageableResponse;
import com.eventify.eventify_backend.controller.EventController;
import com.eventify.eventify_backend.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Test
    void getEvents_ShouldReturnPageableResponse() {
        PageableResponse<EventResponse> expectedResponse = PageableResponse.<EventResponse>builder()
                .content(List.of())
                .pageNumber(0)
                .pageSize(20)
                .totalElements(0)
                .totalPages(0)
                .build();

        when(eventService.getAllEvents(any(), any(), anyInt(), anyInt(), anyString()))
                .thenReturn(expectedResponse);

        PageableResponse<EventResponse> response = eventController.getEvents(null, null, 0, 20, "dateTime");

        assertThat(response).isNotNull();
    }

    @Test
    void getEventById_ShouldReturnEventResponse() {
        EventResponse expectedResponse = EventResponse.builder()
                .id(1L)
                .title("Test Event")
                .build();

        when(eventService.getEventById(1L)).thenReturn(expectedResponse);

        EventResponse response = eventController.getEventById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }
}