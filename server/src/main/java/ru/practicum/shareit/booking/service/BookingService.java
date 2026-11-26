package ru.practicum.shareit.booking.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    public BookingDto create(Integer bookerId, BookingDto dto) {
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getAvailable()) throw new IllegalArgumentException("Item not available");
        if (item.getOwnerId().equals(bookerId)) throw new IllegalArgumentException("Owner cannot book own item");
        if (dto.getStart().isAfter(dto.getEnd()) || dto.getStart().isEqual(dto.getEnd())) throw new IllegalArgumentException("Invalid dates");

        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItemId(dto.getItemId());
        booking.setBookerId(bookerId);
        booking.setStatus(Booking.BookingStatus.WAITING);
        return mapToDto(bookingRepository.save(booking));
    }

    public BookingDto approve(Integer bookingId, Integer ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getOwnerId().equals(ownerId)) throw new IllegalArgumentException("Not owner");
        
        booking.setStatus(approved ? Booking.BookingStatus.APPROVED : Booking.BookingStatus.REJECTED);
        return mapToDto(bookingRepository.save(booking));
    }

    public BookingDto getById(Integer id, Integer userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!booking.getBookerId().equals(userId) && !item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Access denied");
        }
        return mapToDto(booking);
    }

    public List<BookingDto> getByBooker(Integer bookerId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndStartBeforeOrderByStartDesc(bookerId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Booking.BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Booking.BookingStatus.REJECTED);
        };
        return bookings.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<BookingDto> getByOwner(Integer ownerId, BookingState state) {
        List<Integer> itemIds = itemRepository.findByOwnerId(ownerId).stream().map(Item::getId).collect(Collectors.toList());
        if (itemIds.isEmpty()) return List.of();
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItemIdInAndBookerIdNotOrderByStartDesc(itemIds, ownerId);
            case CURRENT -> bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemIds, now, now);
            case PAST -> bookingRepository.findByItemIdInAndStartBeforeOrderByStartDesc(itemIds, now);
            case FUTURE -> bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemIds, now);
            case WAITING -> bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Booking.BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Booking.BookingStatus.REJECTED);
        };
        return bookings.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private BookingDto mapToDto(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setStart(b.getStart());
        dto.setEnd(b.getEnd());
        dto.setItemId(b.getItemId());
        return dto;
    }
}
