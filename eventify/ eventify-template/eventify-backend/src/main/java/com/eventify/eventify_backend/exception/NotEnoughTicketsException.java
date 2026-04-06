
package com.eventify.eventify_backend.exception;

public class NotEnoughTicketsException extends BusinessException {

    public NotEnoughTicketsException(String eventTitle, int available, int requested) {
        super(
                String.format("Not enough tickets for event '%s'. Available: %d, Requested: %d",
                        eventTitle, available, requested),
                "NOT_ENOUGH_TICKETS",
                "ERROR"
        );
    }
}