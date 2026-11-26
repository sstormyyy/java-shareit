package ru.practicum.shareit.item.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @RequestBody ItemDto dto) {
        return itemService.create(ownerId, dto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @PathVariable Integer id, @RequestBody ItemDto dto) {
        return itemService.update(id, ownerId, dto);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Integer id) { return itemService.getById(id); }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemService.getByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) { return itemService.search(text); }
}
