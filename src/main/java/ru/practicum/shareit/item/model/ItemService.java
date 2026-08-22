package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto dto);
    ItemDto update(Long ownerId, Long itemId, ItemDto dto);
    ItemDto findById(Long itemId);
    List<ItemDto> findByOwner(Long ownerId);
    List<ItemDto> search(String text);
}