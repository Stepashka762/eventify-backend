package com.eventify.eventify_backend.scheduler;

import com.eventify.eventify_backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCleanupScheduler {

    private final BookingService bookingService;

    @Scheduled(fixedDelay = 60000)
    public void cleanupExpiredBookings() {
        log.debug("Running expired bookings cleanup...");
        int cleaned = bookingService.cleanupExpiredBookings();
        if (cleaned > 0) {
            log.info("Cleaned up {} expired bookings", cleaned);
        }
    }
}