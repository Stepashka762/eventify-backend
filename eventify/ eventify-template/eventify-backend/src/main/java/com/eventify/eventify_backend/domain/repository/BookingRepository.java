package com.eventify.eventify_backend.domain.repository;

import com.eventify.eventify_backend.domain.model.Booking;
import com.eventify.eventify_backend.domain.model.User;
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
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    Optional<Booking> findByIdAndUser(Long id, User user);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.confirmed = true")
    List<Booking> findConfirmedByUser(@Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.confirmed = false AND b.cancelled = false AND b.expiryTime > CURRENT_TIMESTAMP")
    List<Booking> findPendingByUser(@Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.event.id = :eventId AND b.confirmed = false AND b.cancelled = false AND b.expiryTime < :now")
    List<Booking> findExpiredUnconfirmedBookings(@Param("eventId") Long eventId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.confirmed = false AND b.cancelled = false AND b.expiryTime < :now")
    List<Booking> findAllExpiredUnconfirmedBookings(@Param("now") LocalDateTime now);

    Page<Booking> findByEventId(Long eventId, Pageable pageable);

    Page<Booking> findByConfirmedFalseAndCancelledFalse(Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.event.id = :eventId AND b.confirmed = :confirmed")
    Page<Booking> findByEventIdAndConfirmed(@Param("eventId") Long eventId, @Param("confirmed") Boolean confirmed, Pageable pageable);

    @Modifying
    @Query("UPDATE Booking b SET b.cancelled = true, b.cancelledAt = :now WHERE b.id = :bookingId AND b.user = :user AND b.confirmed = false")
    int cancelBookingByIdAndUser(@Param("bookingId") Long bookingId, @Param("user") User user, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Booking b SET b.cancelled = true, b.cancelledAt = :now WHERE b.event.id = :eventId AND b.confirmed = false AND b.expiryTime < :now AND b.cancelled = false")
    int cancelExpiredBookingsForEvent(@Param("eventId") Long eventId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Booking b SET b.confirmed = true WHERE b.id = :bookingId AND b.confirmed = false AND b.cancelled = false AND b.expiryTime > :now")
    int confirmBookingById(@Param("bookingId") Long bookingId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Booking b SET b.cancelled = true, b.cancelledAt = :now WHERE b.id = :bookingId AND b.confirmed = false")
    int adminCancelBooking(@Param("bookingId") Long bookingId, @Param("now") LocalDateTime now);

    long countByEventIdAndConfirmedTrue(Long eventId);

    @Query("SELECT COALESCE(SUM(b.ticketCount), 0) FROM Booking b WHERE b.event.id = :eventId AND b.confirmed = true")
    Integer getTotalConfirmedTicketsForEvent(@Param("eventId") Long eventId);

    @Query("SELECT b FROM Booking b WHERE b.event.id = :eventId AND b.confirmed = true")
    List<Booking> findConfirmedByEventId(@Param("eventId") Long eventId);

    Optional<Booking> findByUserIdAndEventId(Long userId, Long eventId);
}