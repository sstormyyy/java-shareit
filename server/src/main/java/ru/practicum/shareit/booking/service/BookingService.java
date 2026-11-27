
package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto create(Integer bookerId, BookingDto dto) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("User not found");
        }

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (item.getOwnerId().equals(bookerId)) {
            throw new IllegalArgumentException("Owner cannot book own item");
        }

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new IllegalArgumentException("Item not available");
        }

        if (dto.getStart() == null || dto.getEnd() == null
                || !dto.getStart().isBefore(dto.getEnd())) {
            throw new IllegalArgumentException("Invalid booking dates");
        }

        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItemId(item.getId());
        booking.setBookerId(bookerId);
        booking.setStatus(Booking.BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return mapToDto(savedBooking);
    }

    public BookingDto approve(Integer bookingId, Integer ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException(
                    "Booking does not belong to this owner");
        }

        if (booking.getStatus() != Booking.BookingStatus.WAITING) {
            throw new IllegalArgumentException("Booking is already processed");
        }

        if (approved == null) {
            throw new IllegalArgumentException("Approved parameter is required");
        }

        booking.setStatus(approved
                ? Booking.BookingStatus.APPROVED
                : Booking.BookingStatus.REJECTED);

        return mapToDto(bookingRepository.save(booking));
    }

    public BookingDto getById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!booking.getBookerId().equals(userId)
                && !item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Booking not found");
        }

        return mapToDto(booking);
    }

    public List<BookingDto> getByBooker(Integer bookerId, BookingState state) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("User not found");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository
                        .findByBookerIdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                bookerId, now, now);
                break;
            case PAST:
                bookings = bookingRepository
                        .findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(
                                bookerId, Booking.BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(
                                bookerId, Booking.BookingStatus.REJECTED);
                break;
            default:
                bookings = List.of();
        }

        return bookings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getByOwner(Integer ownerId, BookingState state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found");
        }

        List<Integer> itemIds = itemRepository.findByOwnerId(ownerId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        if (itemIds.isEmpty()) {
            return List.of();
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository
                        .findByItemIdInAndBookerIdNotOrderByStartDesc(itemIds, ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                                itemIds, now, now);
                break;
            case PAST:
                bookings = bookingRepository
                        .findByItemIdInAndEndBeforeOrderByStartDesc(itemIds, now);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findByItemIdInAndStartAfterOrderByStartDesc(itemIds, now);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findByItemIdInAndStatusOrderByStartDesc(
                                itemIds, Booking.BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findByItemIdInAndStatusOrderByStartDesc(
                                itemIds, Booking.BookingStatus.REJECTED);
                break;
            default:
                bookings = List.of();
        }

        return bookings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private BookingDto mapToDto(Booking booking) {
        BookingDto dto = new BookingDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItemId(booking.getItemId());
        dto.setStatus(booking.getStatus().name());

        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new NotFoundException("Booker not found"));

        UserDto bookerDto = new UserDto();
        bookerDto.setId(booker.getId());
        bookerDto.setName(booker.getName());
        bookerDto.setEmail(booker.getEmail());
        dto.setBooker(bookerDto);

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequestId());
        dto.setItem(itemDto);

        return dto;
    }
}
