
package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerIdOrderByStartDesc(Integer bookerId);

    List<Booking> findByItemIdInAndBookerIdNotOrderByStartDesc(
            List<Integer> itemIds, Integer bookerId);

    List<Booking> findByBookerIdAndStartBeforeOrderByStartDesc(
            Integer bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            Integer bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(
            Integer bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Integer bookerId, Booking.BookingStatus status);

    List<Booking> findByItemIdInAndStartBeforeOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime now);

    List<Booking> findByItemIdInAndStartAfterOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime now);

    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemIdInAndEndBeforeOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime now);

    List<Booking> findByItemIdInAndStatusOrderByStartDesc(
            List<Integer> itemIds, Booking.BookingStatus status);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
            Integer itemId,
            Integer bookerId,
            Booking.BookingStatus status,
            LocalDateTime now
    );

    List<Booking> findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
            Integer itemId,
            Booking.BookingStatus status,
            LocalDateTime now
    );

    List<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Integer itemId,
            Booking.BookingStatus status,
            LocalDateTime now
    );
}
