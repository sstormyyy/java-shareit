
package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto create(Integer requestorId, ItemRequestDto dto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestorId(requestorId);
        request.setCreated(LocalDateTime.now());

        return mapToDto(requestRepository.save(request));
    }

    public List<ItemRequestWithItemsDto> getAllByRequestor(Integer requestorId) {
        List<ItemRequest> requests =
                requestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);

        return mapToDtoWithItems(requests);
    }

    public List<ItemRequestDto> getAllOthers(Integer requestorId) {
        return requestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ItemRequestWithItemsDto getById(Integer requestId) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        return mapToDtoWithItems(List.of(request)).get(0);
    }

    private ItemRequestDto mapToDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        return dto;
    }

    private List<ItemRequestWithItemsDto> mapToDtoWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Integer> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findByRequestIdIn(requestIds);

        Map<Integer, List<ItemDto>> itemsByRequestId = items.stream()
                .map(this::mapItemToDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(request -> {
                    ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto();
                    dto.setId(request.getId());
                    dto.setDescription(request.getDescription());
                    dto.setCreated(request.getCreated());
                    dto.setItems(itemsByRequestId.getOrDefault(
                            request.getId(), List.of()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private ItemDto mapItemToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequestId());

        return dto;
    }
}
