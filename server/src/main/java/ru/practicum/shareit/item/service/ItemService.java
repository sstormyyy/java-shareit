
package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentService commentService;

    public ItemDto create(Integer ownerId, ItemDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwnerId(ownerId);
        item.setRequestId(dto.getRequestId());

        if (dto.getRequestId() != null) {
            requestRepository.findById(dto.getRequestId())
                    .orElseThrow(() ->
                            new NotFoundException("Request not found"));
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

    public ItemDto getById(Integer id, Integer userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        ItemDto dto = mapToDto(item);

        if (item.getOwnerId().equals(userId)) {
            addBookingInfo(dto, item.getId());
        }

        return dto;
    }

    public List<ItemDto> getByOwner(Integer ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> {
                    ItemDto dto = mapToDto(item);
                    addBookingInfo(dto, item.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository
                .findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        text, text)
                .stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ItemDto mapToDto(Item item) {
        ItemDto dto = new ItemDto();

        dto.setId(item.getId());
        dto.setOwnerId(item.getOwnerId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequestId());

        List<CommentDto> comments =
                commentService.getByItemId(item.getId());

        dto.setComments(comments);

        return dto;
    }

    private void addBookingInfo(ItemDto dto, Integer itemId) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> lastBookings =
                bookingRepository
                        .findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                                itemId,
                                Booking.BookingStatus.APPROVED,
                                now
                        );

        if (!lastBookings.isEmpty()) {
            Booking lastBooking = lastBookings.get(0);

            ItemDto.BookingInfo lastInfo = new ItemDto.BookingInfo();
            lastInfo.setId(lastBooking.getId());
            lastInfo.setBookerId(lastBooking.getBookerId());

            dto.setLastBooking(lastInfo);
        }

        List<Booking> nextBookings =
                bookingRepository
                        .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                                itemId,
                                Booking.BookingStatus.APPROVED,
                                now
                        );

        if (!nextBookings.isEmpty()) {
            Booking nextBooking = nextBookings.get(0);

            ItemDto.BookingInfo nextInfo = new ItemDto.BookingInfo();
            nextInfo.setId(nextBooking.getId());
            nextInfo.setBookerId(nextBooking.getBookerId());

            dto.setNextBooking(nextInfo);
        }
    }
}
