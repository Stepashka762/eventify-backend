package com.eventify.eventify_backend.exception;

public class BookingAlreadyConfirmedException extends BusinessException {

    public BookingAlreadyConfirmedException(Long bookingId) {
        super(
                String.format("Booking with id %d is already confirmed", bookingId),
                "BOOKING_ALREADY_CONFIRMED",
                "WARNING"
        );
    }
}