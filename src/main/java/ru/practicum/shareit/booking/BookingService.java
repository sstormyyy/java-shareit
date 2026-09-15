package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemShortDto;
import ru.practicum.shareit.booking.dto.UserShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          ItemRepository itemRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingDto create(Long bookerId, BookingCreateDto dto) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Даты не указаны");
        }
        if (!dto.getStart().isBefore(dto.getEnd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата начала должна быть раньше даты конца");
        }
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата начала не может быть в прошлом");
        }

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вещь недоступна для бронирования");
        }
        if (item.getOwnerId().equals(bookerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Нельзя забронировать свою вещь");
        }

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItemId(item.getId());
        booking.setBookerId(bookerId);
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);
        return mapToDto(saved, item, booker);
    }

    @Transactional
    public BookingDto approve(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирование не найдено"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));

        if (!item.getOwnerId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Только владелец может подтверждать бронирование");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking saved = bookingRepository.save(booking);
        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        return mapToDto(saved, item, booker);
    }

    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирование не найдено"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));
        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        if (!booking.getBookerId().equals(userId) && !item.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирование не найдено");
        }

        return mapToDto(booking, item, booker);
    }

    public List<BookingDto> getBookingsByBooker(Long bookerId, BookingState state) {
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(bookerId, now, BookingStatus.APPROVED);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookings(bookerId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookings(bookerId, now);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
        }
        return bookings.stream().map(b -> {
            Item item = itemRepository.findById(b.getItemId()).orElse(null);
            User booker = userRepository.findById(b.getBookerId()).orElse(null);
            return mapToDto(b, item, booker);
        }).collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsByOwner(Long ownerId, BookingState state) {
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByItemOwnerId(ownerId, now, BookingStatus.APPROVED);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByItemOwnerId(ownerId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByItemOwnerId(ownerId, now);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerId(ownerId);
        }
        return bookings.stream().map(b -> {
            Item item = itemRepository.findById(b.getItemId()).orElse(null);
            User booker = userRepository.findById(b.getBookerId()).orElse(null);
            return mapToDto(b, item, booker);
        }).collect(Collectors.toList());
    }

    private BookingDto mapToDto(Booking booking, Item item, User booker) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item != null ? new ItemShortDto(item.getId(), item.getName()) : null,
                booker != null ? new UserShortDto(booker.getId(), booker.getName()) : null,
                booking.getStatus()
        );
    }
}