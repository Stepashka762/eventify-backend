package com.eventify.eventify_backend.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "total_tickets", nullable = false)
    private Integer totalTickets;

    @Column(name = "available_tickets", nullable = false)
    private Integer availableTickets;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();



    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setEvent(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setEvent(null);
    }

    public boolean hasEnoughTickets(int requestedCount) {
        return availableTickets >= requestedCount;
    }

    public void reserveTickets(int count) {
        if (!hasEnoughTickets(count)) {
            throw new IllegalStateException("Not enough tickets available. Available: " + availableTickets + ", Requested: " + count);
        }
        availableTickets -= count;
    }

    public void releaseTickets(int count) {
        int newAvailable = availableTickets + count;
        if (newAvailable > totalTickets) {
            throw new IllegalStateException("Cannot release more tickets than total. Total: " + totalTickets + ", Available: " + availableTickets + ", To release: " + count);
        }
        availableTickets = newAvailable;
    }

    public boolean isUpcoming() {
        return dateTime.isAfter(LocalDateTime.now());
    }

    public boolean hasStarted() {
        return dateTime.isBefore(LocalDateTime.now());
    }

    public int getSoldTickets() {
        return totalTickets - availableTickets;
    }

    public double getOccupancyPercentage() {
        if (totalTickets == 0) return 0.0;
        return (double) getSoldTickets() / totalTickets * 100;
    }

    public boolean isSoldOut() {
        return availableTickets == 0;
    }
}