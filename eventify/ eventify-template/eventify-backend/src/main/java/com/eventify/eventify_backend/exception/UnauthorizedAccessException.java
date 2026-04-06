
package com.eventify.eventify_backend.exception;

public class UnauthorizedAccessException extends BusinessException {

    public UnauthorizedAccessException(String message) {
        super(message, "UNAUTHORIZED_ACCESS", "ERROR");
    }

    public UnauthorizedAccessException(Long bookingId, Long userId) {
        super(
                String.format("User %d does not have access to booking %d", userId, bookingId),
                "UNAUTHORIZED_ACCESS",
                "ERROR"
        );
    }
}