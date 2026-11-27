
package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    public ItemClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(
                new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ResponseEntity<Object> create(int userId, ItemDto dto) {
        return post("/items", dto,
                Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> update(int userId, int id, ItemDto dto) {
        return patch("/items/" + id, dto,
                Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> getById(int userId, int id) {
        return get("/items/" + id, null,
                Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> getByOwner(int userId) {
        return get("/items", null,
                Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> search(String text) {
        return get("/items/search", Map.of("text", text));
    }

    public ResponseEntity<Object> createComment(
            int userId, int itemId, CommentDto dto) {
        return post("/items/" + itemId + "/comment", dto,
                Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }
}
