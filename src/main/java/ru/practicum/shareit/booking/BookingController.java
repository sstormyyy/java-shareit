package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItConstants;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(
            @RequestHeader(ShareItConstants.X_SHARER_USER_ID) Long userId,
            @RequestBody BookingCreateDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(
            @RequestHeader(ShareItConstants.X_SHARER_USER_ID) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(
            @RequestHeader(ShareItConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(
            @RequestHeader(ShareItConstants.X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(
            @RequestHeader(ShareItConstants.X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}