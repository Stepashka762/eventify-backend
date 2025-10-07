package com.eventify.eventify_backend.controller;

import com.eventify.eventify_backend.api.dto.request.EventCreateRequest;
import com.eventify.eventify_backend.api.dto.request.EventUpdateRequest;
import com.eventify.eventify_backend.api.dto.response.BookingResponse;
import com.eventify.eventify_backend.api.dto.response.EventResponse;
import com.eventify.eventify_backend.api.dto.response.PageableResponse;
import com.eventify.eventify_backend.service.BookingService;
import com.eventify.eventify_backend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Административные функции")
public class AdminController {

    private final EventService eventService;
    private final BookingService bookingService;



    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать мероприятие (Admin)")
    public EventResponse createEvent(@Valid @RequestBody EventCreateRequest request) {
        log.info("POST /admin/events - title: {}", request.getTitle());
        return eventService.createEvent(request);
    }

    @PutMapping("/events/{id}")
    @Operation(summary = "Обновить мероприятие (Admin)")
    public EventResponse updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateRequest request) {
        log.info("PUT /admin/events/{}", id);
        return eventService.updateEvent(id, request);
    }

    @DeleteMapping("/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить мероприятие (Admin)")
    public void deleteEvent(@PathVariable Long id) {
        log.info("DELETE /admin/events/{}", id);
        eventService.deleteEvent(id);
    }


    @GetMapping("/bookings")
    @Operation(summary = "Получить все бронирования (Admin)")
    public PageableResponse<BookingResponse> getAllBookings(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Boolean unconfirmedOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {
        log.info("GET /admin/bookings - eventId: {}, unconfirmedOnly: {}", eventId, unconfirmedOnly);
        return bookingService.getAllBookings(eventId, unconfirmedOnly, page, size, sort);
    }

    @PutMapping("/bookings/{id}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Подтвердить бронирование (Admin)")
    public void confirmBooking(@PathVariable Long id) {
        log.info("PUT /admin/bookings/{}/confirm", id);
        bookingService.confirmBooking(id);
    }

    @DeleteMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить бронирование (Admin)")
    public void deleteBooking(@PathVariable Long id) {
        log.info("DELETE /admin/bookings/{}", id);
        bookingService.adminDeleteBooking(id);
    }
}