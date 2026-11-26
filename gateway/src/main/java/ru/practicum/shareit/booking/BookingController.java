package ru.practicum.shareit.booking;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BookingClient;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody BookingDto dto) {
        return bookingClient.create(dto, userId.toString());
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer bookingId, @RequestParam Boolean approved) {
        return bookingClient.approve(bookingId, approved, userId.toString());
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer bookingId) {
        return bookingClient.getById(bookingId, userId.toString());
    }

    @GetMapping
    public ResponseEntity<Object> getByBooker(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.getByBooker(userId.toString(), state.name());
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.getByOwner(userId.toString(), state.name());
    }
}
