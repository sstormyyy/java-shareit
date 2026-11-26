package ru.practicum.shareit.item.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;

    public ItemDto create(Integer ownerId, ItemDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwnerId(ownerId);
        item.setRequestId(dto.getRequestId());

        if (dto.getRequestId() != null) {
            requestRepository.findById(dto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
        }
        return mapToDto(itemRepository.save(item));
    }

    public ItemDto update(Integer id, Integer ownerId, ItemDto dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not owner");
        }
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        return mapToDto(itemRepository.save(item));
    }

    public ItemDto getById(Integer id) {
        return mapToDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found")));
    }

    public List<ItemDto> getByOwner(Integer ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private ItemDto mapToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequestId());
        return dto;
    }
}