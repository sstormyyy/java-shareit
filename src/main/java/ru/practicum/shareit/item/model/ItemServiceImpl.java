package ru.practicum.shareit.item.model;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public ItemDto create(Long ownerId, ItemDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Название не может быть пустым");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Описание не может быть пустым");
        }
        if (dto.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Статус доступности должен быть указан");
        }

        Item item = new Item(null, dto.getName(), dto.getDescription(), dto.getAvailable(), ownerId);
        Item created = itemRepository.save(item);
        return new ItemDto(created.getId(), created.getName(), created.getDescription(), created.getAvailable(), created.getOwnerId(), List.of());
    }

    @Transactional
    public ItemDto update(Long ownerId, Long itemId, ItemDto dto) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Только владелец может редактировать вещь");
        }

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) existing.setAvailable(dto.getAvailable());

        Item updated = itemRepository.save(existing);
        return new ItemDto(updated.getId(), updated.getName(), updated.getDescription(), updated.getAvailable(), updated.getOwnerId(), getCommentsForItem(updated.getId()));
    }

    public ItemDto findById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена"));
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwnerId(), getCommentsForItem(item.getId()));
    }

    public List<ItemDto> findByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwnerId(), getCommentsForItem(item.getId())))
                .collect(Collectors.toList());
    }

    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text).stream()
                .map(item -> new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwnerId(), List.of()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Long itemId, Long userId, String text) {
        if (text == null || text.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Текст комментария не может быть пустым");
        }

        boolean hasApprovedBooking = bookingRepository.findByItemIdAndStatusOrderByStartDesc(itemId, BookingStatus.APPROVED)
                .stream()
                .anyMatch(b -> b.getBookerId().equals(userId) && b.getEnd().isBefore(LocalDateTime.now()));

        if (!hasApprovedBooking) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя оставить отзыв, если вы не бронировали вещь");
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        comment.setAuthorName("User " + userId);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return new CommentDto(saved.getId(), saved.getText(), saved.getAuthorName(), saved.getCreated());
    }

    private List<CommentDto> getCommentsForItem(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
                .map(c -> new CommentDto(c.getId(), c.getText(), c.getAuthorName(), c.getCreated()))
                .collect(Collectors.toList());
    }
}