
package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    private Integer itemId;

    private Integer bookerId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public enum BookingStatus {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}
