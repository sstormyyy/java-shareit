package ru.practicum.shareit.item;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto dto) {
        return itemClient.create(dto, userId.toString());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id, @Valid @RequestBody ItemDto dto) {
        return itemClient.update(id, dto, userId.toString());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) { return itemClient.getById(id); }

    @GetMapping
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.getByOwner(userId.toString());
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) { return itemClient.search(text); }
}
