package com.eventify.eventify_backend.exception;

public class BookingExpiredException extends BusinessException {

    public BookingExpiredException(Long bookingId) {
        super(
                String.format("Booking with id %d has expired", bookingId),
                "BOOKING_EXPIRED",
                "ERROR"
        );
    }
}