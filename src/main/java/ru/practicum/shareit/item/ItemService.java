package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);
    ItemDto update(ItemDto itemDto, Long userId, Long itemId);
    ItemDto getById(Long itemId);
    List<ItemDto> getByOwnerId(Long ownerId);
    List<ItemDto> search(String text);
}