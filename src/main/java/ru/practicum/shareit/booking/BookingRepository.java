package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = ?1 AND b.start <= ?2 AND b.end >= ?2 AND b.status = ?3 ORDER BY b.start DESC")
    List<Booking> findCurrentBookings(Long bookerId, LocalDateTime now, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findPastBookings(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findFutureBookings(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN (SELECT i.id FROM Item i WHERE i.ownerId = ?1) ORDER BY b.start DESC")
    List<Booking> findByItemOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN (SELECT i.id FROM Item i WHERE i.ownerId = ?1) AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN (SELECT i.id FROM Item i WHERE i.ownerId = ?1) AND b.start <= ?2 AND b.end >= ?2 AND b.status = ?3 ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByItemOwnerId(Long ownerId, LocalDateTime now, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN (SELECT i.id FROM Item i WHERE i.ownerId = ?1) AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findPastBookingsByItemOwnerId(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN (SELECT i.id FROM Item i WHERE i.ownerId = ?1) AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByItemOwnerId(Long ownerId, LocalDateTime now);

    // Новый метод для проверки завершенного бронирования
    Optional<Booking> findByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime endBefore);
}