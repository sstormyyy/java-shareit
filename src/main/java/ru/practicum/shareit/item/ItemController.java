package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @RequestHeader(ShareItConstants.X_SHARER_USER_ID) Long ownerId,
            @RequestBody ItemDto dto) {
        return itemService.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader(ShareItConstants.X_SHARER_USER_ID) Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto dto) {
        return itemService.update(ownerId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<ItemDto> findByOwner(
            @RequestHeader(ShareItConstants.X_SHARER_USER_ID) Long ownerId) {
        return itemService.findByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @RequestHeader(ShareItConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody Map<String, String> body) {
        String text = body.get("text");
        return itemService.addComment(itemId, userId, text);
    }
}