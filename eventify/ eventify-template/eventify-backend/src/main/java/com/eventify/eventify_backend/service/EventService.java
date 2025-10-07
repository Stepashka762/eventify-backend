package com.eventify.eventify_backend.service;

import com.eventify.eventify_backend.api.dto.request.EventCreateRequest;
import com.eventify.eventify_backend.api.dto.request.EventUpdateRequest;
import com.eventify.eventify_backend.api.dto.response.EventResponse;
import com.eventify.eventify_backend.api.dto.response.PageableResponse;
import com.eventify.eventify_backend.domain.model.Event;
import com.eventify.eventify_backend.domain.repository.EventRepository;
import com.eventify.eventify_backend.exception.ResourceNotFoundException;
import com.eventify.eventify_backend.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        log.debug("Creating new event: {}", request.getTitle());

        Event event = eventMapper.toEntity(request);
        Event savedEvent = eventRepository.save(event);

        log.info("Event created successfully with id: {}", savedEvent.getId());
        return eventMapper.toResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public PageableResponse<EventResponse> getAllEvents(LocalDateTime from, LocalDateTime to, int page, int size, String sort) {
        log.debug("Getting all events with pagination - page: {}, size: {}, sort: {}", page, size, sort);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Event> eventPage;

        if (from != null && to != null) {
            eventPage = eventRepository.findByDateTimeBetween(from, to, pageable);
        } else if (from != null) {
            eventPage = eventRepository.findByDateTimeBetween(from, LocalDateTime.MAX, pageable);
        } else if (to != null) {
            eventPage = eventRepository.findByDateTimeBetween(LocalDateTime.MIN, to, pageable);
        } else {
            eventPage = eventRepository.findUpcomingEvents(pageable);
        }

        return toPageableResponse(eventPage.map(eventMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        log.debug("Getting event by id: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        return eventMapper.toResponse(event);
    }

    @Transactional(readOnly = true)
    public Event getEventEntityById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventUpdateRequest request) {
        log.debug("Updating event with id: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        eventMapper.updateEntity(event, request);


        if (request.getTotalTickets() != null) {
            int oldTotal = event.getTotalTickets();
            int newTotal = request.getTotalTickets();
            int soldTickets = event.getSoldTickets();

            if (newTotal < soldTickets) {
                throw new IllegalStateException(
                        String.format("Cannot reduce total tickets below already sold tickets. Sold: %d, New total: %d",
                                soldTickets, newTotal)
                );
            }

            event.setAvailableTickets(newTotal - soldTickets);
            event.setTotalTickets(newTotal);
        }

        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated successfully with id: {}", id);

        return eventMapper.toResponse(updatedEvent);
    }

    @Transactional
    public void deleteEvent(Long id) {
        log.debug("Deleting event with id: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        eventRepository.delete(event);
        log.info("Event deleted successfully with id: {}", id);
    }

    @Transactional
    public void reserveTickets(Long eventId, int ticketCount) {
        int updated = eventRepository.decrementAvailableTickets(eventId, ticketCount);
        if (updated == 0) {
            Event event = getEventEntityById(eventId);
            throw new IllegalStateException(
                    String.format("Failed to reserve tickets. Event: %s, Available: %d, Requested: %d",
                            event.getTitle(), event.getAvailableTickets(), ticketCount)
            );
        }
    }

    @Transactional
    public void releaseTickets(Long eventId, int ticketCount) {
        eventRepository.incrementAvailableTickets(eventId, ticketCount);
    }

    private PageableResponse<EventResponse> toPageableResponse(Page<EventResponse> page) {
        return PageableResponse.<EventResponse>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}