package com.eventify.eventify_backend.service;

import com.eventify.eventify_backend.api.dto.request.CreateBookingRequest;
import com.eventify.eventify_backend.api.dto.request.UpdateBookingRequest;
import com.eventify.eventify_backend.api.dto.response.BookingResponse;
import com.eventify.eventify_backend.api.dto.response.PageableResponse;
import com.eventify.eventify_backend.domain.model.Booking;
import com.eventify.eventify_backend.domain.model.Event;
import com.eventify.eventify_backend.domain.model.User;
import com.eventify.eventify_backend.domain.repository.BookingRepository;
import com.eventify.eventify_backend.exception.*;
import com.eventify.eventify_backend.mapper.BookingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventService eventService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    private static final int BOOKING_EXPIRY_MINUTES = 15;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request, Long userId) {
        log.debug("Creating booking for user: {}, event: {}, tickets: {}",
                userId, request.getEventId(), request.getTicketCount());

        User user = userService.findById(userId);
        Event event = eventService.getEventEntityById(request.getEventId());

        if (!event.hasEnoughTickets(request.getTicketCount())) {
            throw new NotEnoughTicketsException(
                    event.getTitle(),
                    event.getAvailableTickets(),
                    request.getTicketCount()
            );
        }

        if (bookingRepository.findByUserIdAndEventId(userId, request.getEventId()).isPresent()) {
            throw new BusinessException(
                    "You already have a pending booking for this event",
                    "BOOKING_ALREADY_EXISTS",
                    "WARNING"
            );
        }

        event.reserveTickets(request.getTicketCount());
        eventService.reserveTickets(event.getId(), request.getTicketCount());

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .ticketCount(request.getTicketCount())
                .expiryTime(LocalDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES))
                .confirmed(false)
                .cancelled(false)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        user.addBooking(savedBooking);
        event.addBooking(savedBooking);

        log.info("Booking created successfully with id: {}", savedBooking.getId());
        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId, Long userId, boolean isAdmin) {
        log.debug("Getting booking by id: {} for user: {}", bookingId, userId);
        Booking booking = getBookingWithAccessCheck(bookingId, userId, isAdmin);
        return bookingMapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId) {
        log.debug("Getting all bookings for user: {}", userId);
        User user = userService.findById(userId);
        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Transactional
    public BookingResponse updateBooking(Long bookingId, UpdateBookingRequest request, Long userId, boolean isAdmin) {
        log.debug("Updating booking: {} for user: {}", bookingId, userId);
        Booking booking = getBookingWithAccessCheck(bookingId, userId, isAdmin);

        if (!booking.canBeModified()) {
            throw new BusinessException(
                    "Booking cannot be modified. Current status: " + booking.getStatus(),
                    "BOOKING_CANNOT_BE_MODIFIED",
                    "WARNING"
            );
        }

        int oldTicketCount = booking.getTicketCount();
        int newTicketCount = request.getTicketCount();
        int difference = newTicketCount - oldTicketCount;

        if (difference > 0) {
            Event event = eventService.getEventEntityById(booking.getEvent().getId());
            if (!event.hasEnoughTickets(difference)) {
                throw new NotEnoughTicketsException(
                        event.getTitle(),
                        event.getAvailableTickets(),
                        difference
                );
            }
            event.reserveTickets(difference);
            eventService.reserveTickets(event.getId(), difference);
        } else if (difference < 0) {
            int ticketsToReturn = -difference;
            eventService.releaseTickets(booking.getEvent().getId(), ticketsToReturn);
            booking.getEvent().releaseTickets(ticketsToReturn);
        }

        booking.setTicketCount(newTicketCount);
        booking.setExpiryTime(LocalDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES));
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking updated successfully: {}, ticket count changed from {} to {}",
                bookingId, oldTicketCount, newTicketCount);
        return bookingMapper.toResponse(updatedBooking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId, boolean isAdmin) {
        log.debug("Cancelling booking: {} for user: {}", bookingId, userId);
        Booking booking = getBookingWithAccessCheck(bookingId, userId, isAdmin);

        if (booking.getConfirmed()) {
            throw new BookingAlreadyConfirmedException(bookingId);
        }

        if (booking.getCancelled()) {
            throw new BusinessException(
                    "Booking is already cancelled",
                    "BOOKING_ALREADY_CANCELLED",
                    "WARNING"
            );
        }

        eventService.releaseTickets(booking.getEvent().getId(), booking.getTicketCount());
        booking.getEvent().releaseTickets(booking.getTicketCount());
        booking.cancel();
        bookingRepository.save(booking);

        log.info("Booking cancelled successfully: {}", bookingId);
    }

    @Transactional
    public void confirmBooking(Long bookingId) {
        log.debug("Confirming booking: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (booking.getConfirmed()) {
            throw new BookingAlreadyConfirmedException(bookingId);
        }

        if (booking.isExpired()) {
            throw new BookingExpiredException(bookingId);
        }

        // Просто подтверждаем бронирование
        booking.setConfirmed(true);
        bookingRepository.save(booking);

        log.info("Booking confirmed successfully: {}", bookingId);
    }

    @Transactional
    public void adminDeleteBooking(Long bookingId) {
        log.debug("Admin deleting booking: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        bookingRepository.delete(booking);
        log.info("Booking deleted by admin: {}", bookingId);
    }

    @Transactional(readOnly = true)
    public PageableResponse<BookingResponse> getAllBookings(Long eventId, Boolean unconfirmedOnly,
                                                            int page, int size, String sort) {
        log.debug("Getting all bookings - eventId: {}, unconfirmedOnly: {}, page: {}, size: {}",
                eventId, unconfirmedOnly, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Booking> bookingPage;

        if (eventId != null) {
            if (unconfirmedOnly != null && unconfirmedOnly) {
                bookingPage = bookingRepository.findByEventIdAndConfirmed(eventId, false, pageable);
            } else {
                bookingPage = bookingRepository.findByEventId(eventId, pageable);
            }
        } else if (unconfirmedOnly != null && unconfirmedOnly) {
            bookingPage = bookingRepository.findByConfirmedFalseAndCancelledFalse(pageable);
        } else {
            bookingPage = bookingRepository.findAll(pageable);
        }

        return toPageableResponse(bookingPage.map(bookingMapper::toResponse));
    }

    @Transactional
    public int cleanupExpiredBookings() {
        log.debug("Cleaning up expired bookings");
        List<Booking> expiredBookings = bookingRepository.findAllExpiredUnconfirmedBookings(LocalDateTime.now());
        int count = 0;

        for (Booking booking : expiredBookings) {
            try {
                eventService.releaseTickets(booking.getEvent().getId(), booking.getTicketCount());
                booking.getEvent().releaseTickets(booking.getTicketCount());
                booking.cancel();
                bookingRepository.save(booking);
                count++;
            } catch (Exception e) {
                log.error("Failed to cleanup expired booking: {}", booking.getId(), e);
            }
        }
        log.info("Cleaned up {} expired bookings", count);
        return count;
    }

    private Booking getBookingWithAccessCheck(Long bookingId, Long userId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (!isAdmin && !booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException(bookingId, userId);
        }
        return booking;
    }

    private PageableResponse<BookingResponse> toPageableResponse(Page<BookingResponse> page) {
        return PageableResponse.<BookingResponse>builder()
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