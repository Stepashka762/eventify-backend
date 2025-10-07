package com.eventify.eventify_backend.controller;

import com.eventify.eventify_backend.api.dto.request.CreateBookingRequest;
import com.eventify.eventify_backend.api.dto.request.UpdateBookingRequest;
import com.eventify.eventify_backend.api.dto.response.BookingResponse;
import com.eventify.eventify_backend.security.SecurityUtils;
import com.eventify.eventify_backend.service.BookingService;
import com.eventify.eventify_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Операции с бронированиями")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получить свои бронирования")
    public List<BookingResponse> getUserBookings() {
        String email = SecurityUtils.getCurrentUserEmail();
        Long userId = userService.findByEmail(email).getId();
        log.info("GET /bookings - user: {}", userId);
        return bookingService.getUserBookings(userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить бронирование по ID")
    public BookingResponse getBookingById(@PathVariable Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        Long userId = userService.findByEmail(email).getId();
        boolean isAdmin = SecurityUtils.isAdmin();
        log.info("GET /bookings/{} - user: {}", id, userId);
        return bookingService.getBookingById(id, userId, isAdmin);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать бронирование")
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        Long userId = userService.findByEmail(email).getId();
        log.info("POST /bookings - user: {}, event: {}", userId, request.getEventId());
        return bookingService.createBooking(request, userId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить бронирование")
    public BookingResponse updateBooking(@PathVariable Long id, @Valid @RequestBody UpdateBookingRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        Long userId = userService.findByEmail(email).getId();
        boolean isAdmin = SecurityUtils.isAdmin();
        log.info("PUT /bookings/{} - user: {}", id, userId);
        return bookingService.updateBooking(id, request, userId, isAdmin);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отменить бронирование")
    public void cancelBooking(@PathVariable Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        Long userId = userService.findByEmail(email).getId();
        boolean isAdmin = SecurityUtils.isAdmin();
        log.info("DELETE /bookings/{} - user: {}", id, userId);
        bookingService.cancelBooking(id, userId, isAdmin);
    }
}