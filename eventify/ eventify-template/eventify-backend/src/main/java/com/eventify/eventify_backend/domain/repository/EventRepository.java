package com.eventify.eventify_backend.domain.repository;

import com.eventify.eventify_backend.domain.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {


    Page<Event> findByDateTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.dateTime >= CURRENT_TIMESTAMP ORDER BY e.dateTime ASC")
    Page<Event> findUpcomingEvents(Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.dateTime < CURRENT_TIMESTAMP ORDER BY e.dateTime DESC")
    Page<Event> findPastEvents(Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.id = :id AND e.availableTickets >= :tickets")
    Optional<Event> findByIdAndAvailableTicketsGreaterThanEqual(@Param("id") Long id, @Param("tickets") Integer tickets);

    @Query("SELECT e FROM Event e WHERE e.availableTickets > 0 AND e.dateTime >= CURRENT_TIMESTAMP")
    Page<Event> findAvailableEvents(Pageable pageable);

    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<Event> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Modifying
    @Query("UPDATE Event e SET e.availableTickets = e.availableTickets - :count WHERE e.id = :eventId AND e.availableTickets >= :count")
    int decrementAvailableTickets(@Param("eventId") Long eventId, @Param("count") Integer count);

    @Modifying
    @Query("UPDATE Event e SET e.availableTickets = e.availableTickets + :count WHERE e.id = :eventId AND e.availableTickets + :count <= e.totalTickets")
    int incrementAvailableTickets(@Param("eventId") Long eventId, @Param("count") Integer count);

    @Query("SELECT COALESCE(SUM(b.ticketCount), 0) FROM Booking b WHERE b.event.id = :eventId AND b.confirmed = true")
    Integer getTotalConfirmedTickets(@Param("eventId") Long eventId);
}