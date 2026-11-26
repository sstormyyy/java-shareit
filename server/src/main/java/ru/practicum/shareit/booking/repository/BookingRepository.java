package ru.practicum.shareit.booking.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDesc(Integer bookerId);
    List<Booking> findByItemIdInAndBookerIdNotOrderByStartDesc(List<Integer> itemIds, Integer bookerId);
    List<Booking> findByBookerIdAndStartBeforeOrderByStartDesc(Integer bookerId, LocalDateTime now);
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime now);
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer bookerId, LocalDateTime now, LocalDateTime now2);
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Integer bookerId, Booking.BookingStatus status);
    
    List<Booking> findByItemIdInAndStartBeforeOrderByStartDesc(List<Integer> itemIds, LocalDateTime now);
    List<Booking> findByItemIdInAndStartAfterOrderByStartDesc(List<Integer> itemIds, LocalDateTime now);
    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Integer> itemIds, LocalDateTime now, LocalDateTime now2);
    List<Booking> findByItemIdInAndStatusOrderByStartDesc(List<Integer> itemIds, Booking.BookingStatus status);
}
