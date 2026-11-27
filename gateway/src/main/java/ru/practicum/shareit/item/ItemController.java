
package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @Valid @RequestBody ItemDto dto) {
        return itemClient.create(userId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int id,
            @RequestBody ItemDto dto) {
        return itemClient.update(userId, id, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int id) {
        return itemClient.getById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwner(
            @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemClient.getByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int itemId,
            @RequestBody CommentDto dto) {
        return itemClient.createComment(userId, itemId, dto);
    }
}
