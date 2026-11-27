package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerIdOrderByStartDesc(Integer bookerId);

    List<Booking> findByItemIdInAndBookerIdNotOrderByStartDesc(
            List<Integer> itemIds, Integer bookerId);

    List<Booking> findByBookerIdAndStartBeforeOrderByStartDesc(
            Integer bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = :bookerId "
            + "AND b.start > :now AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findFutureByBookerId(
            @Param("bookerId") Integer bookerId,
            @Param("now") LocalDateTime now,
            @Param("status") Booking.BookingStatus status);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer bookerId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = :bookerId "
            + "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastByBookerId(
            @Param("bookerId") Integer bookerId,
            @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Integer bookerId, Booking.BookingStatus status);

    List<Booking> findByItemIdInAndStartBeforeOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN :itemIds "
            + "AND b.start > :now AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findFutureByItemIds(
            @Param("itemIds") List<Integer> itemIds,
            @Param("now") LocalDateTime now,
            @Param("status") Booking.BookingStatus status);

    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN :itemIds "
            + "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastByItemIds(
            @Param("itemIds") List<Integer> itemIds,
            @Param("now") LocalDateTime now);

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