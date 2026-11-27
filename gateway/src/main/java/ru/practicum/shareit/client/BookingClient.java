package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ResponseEntity<Object> create(int userId, BookingDto dto) {
        return post("/bookings", dto, Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> approve(int userId, int bookingId, boolean approved) {
        return patch("/bookings/" + bookingId, null,
                Map.of("approved", approved),
                Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> getById(int userId, int bookingId) {
        return get("/bookings/" + bookingId, null, Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> getAll(int userId, String state) {
        return get("/bookings", Map.of("state", state), Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> getAllForOwner(int userId, String state) {
        return get("/bookings/owner", Map.of("state", state), Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }
}