package com.eventify.eventify_backend.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Event event;

    @Column(name = "ticket_count", nullable = false)
    private Integer ticketCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    @Builder.Default
    private Boolean confirmed = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean cancelled = false;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @PrePersist
    protected void onCreate() {
        if (expiryTime == null) {
            expiryTime = LocalDateTime.now().plusMinutes(15);
        }
        if (confirmed == null) confirmed = false;
        if (cancelled == null) cancelled = false;
    }

    public void confirm() {
        if (cancelled) {
            throw new IllegalStateException("Cannot confirm a cancelled booking");
        }
        if (confirmed) {
            throw new IllegalStateException("Booking is already confirmed");
        }
        if (isExpired()) {
            throw new IllegalStateException("Cannot confirm an expired booking");
        }
        this.confirmed = true;
    }

    public void cancel() {
        if (cancelled) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (confirmed) {
            throw new IllegalStateException("Cannot cancel a confirmed booking");
        }
        this.cancelled = true;
        this.cancelledAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return !confirmed && !cancelled && expiryTime.isBefore(LocalDateTime.now());
    }

    public boolean isActive() {
        return !cancelled && !confirmed && !isExpired();
    }

    public boolean canBeModified() {
        return !confirmed && !cancelled && !isExpired();
    }

    public String getStatus() {
        if (confirmed) return "CONFIRMED";
        if (cancelled) return "CANCELLED";
        if (isExpired()) return "EXPIRED";
        return "PENDING";
    }
}