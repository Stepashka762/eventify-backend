package com.eventify.eventify_backend.unit.service;

import com.eventify.eventify_backend.api.dto.request.EventCreateRequest;
import com.eventify.eventify_backend.api.dto.response.EventResponse;
import com.eventify.eventify_backend.domain.model.Event;
import com.eventify.eventify_backend.domain.repository.EventRepository;
import com.eventify.eventify_backend.exception.ResourceNotFoundException;
import com.eventify.eventify_backend.mapper.EventMapper;
import com.eventify.eventify_backend.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private EventCreateRequest createRequest;
    private Event event;
    private EventResponse eventResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);

        createRequest = EventCreateRequest.builder()
                .title("Test Event")
                .description("Test Description")
                .dateTime(futureDate)
                .totalTickets(100)
                .build();

        event = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("Test Description")
                .dateTime(futureDate)
                .totalTickets(100)
                .availableTickets(100)
                .build();

        eventResponse = EventResponse.builder()
                .id(1L)
                .title("Test Event")
                .description("Test Description")
                .dateTime(futureDate)
                .totalTickets(100)
                .availableTickets(100)
                .build();
    }

    @Test
    void createEvent_Success_ShouldReturnEventResponse() {
        when(eventMapper.toEntity(createRequest)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toResponse(event)).thenReturn(eventResponse);

        EventResponse result = eventService.createEvent(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(eventRepository).save(event);
    }

    @Test
    void getEventById_EventExists_ShouldReturnEventResponse() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(event)).thenReturn(eventResponse);

        EventResponse result = eventService.getEventById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getEventById_EventNotFound_ShouldThrowException() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteEvent_EventExists_ShouldDelete() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).delete(event);

        eventService.deleteEvent(1L);

        verify(eventRepository).delete(event);
    }

    @Test
    void deleteEvent_EventNotFound_ShouldThrowException() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.deleteEvent(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}