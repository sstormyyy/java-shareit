
package ru.practicum.shareit.item.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @Valid @RequestBody ItemDto dto) {
        return itemService.create(ownerId, dto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @PathVariable Integer id,
            @RequestBody ItemDto dto) {
        return itemService.update(id, ownerId, dto);
    }

    @GetMapping("/{id}")
    public ItemDto getById(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer id) {
        return itemService.getById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getByOwner(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemService.getByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId,
            @RequestBody CommentDto dto) {
        return commentService.create(userId, itemId, dto.getText());
    }
}
