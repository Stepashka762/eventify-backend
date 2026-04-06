package com.eventify.eventify_backend.controller;

import com.eventify.eventify_backend.api.dto.response.EventResponse;
import com.eventify.eventify_backend.api.dto.response.PageableResponse;
import com.eventify.eventify_backend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Управление мероприятиями")
public class EventController {

    private final EventService eventService;

    @GetMapping
    @Operation(summary = "Получить список мероприятий")
    public PageableResponse<EventResponse> getEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime") String sort) {
        log.info("GET /events - page: {}, size: {}", page, size);
        return eventService.getAllEvents(from, to, page, size, sort);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить мероприятие по ID")
    public EventResponse getEventById(@PathVariable Long id) {
        log.info("GET /events/{}", id);
        return eventService.getEventById(id);
    }
}