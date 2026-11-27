
package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public CommentDto create(Integer authorId, Integer itemId, String text) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("User not found");
        }

        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item not found");
        }

        boolean hasCompletedBooking =
                bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                        itemId,
                        authorId,
                        Booking.BookingStatus.APPROVED,
                        LocalDateTime.now()
                );

        if (!hasCompletedBooking) {
            throw new IllegalArgumentException(
                    "Only users who have booked this item can leave comments"
            );
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setItemId(itemId);
        comment.setAuthorId(authorId);
        comment.setCreated(LocalDateTime.now());

        return mapToDto(commentRepository.save(comment));
    }

    public List<CommentDto> getByItemId(Integer itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CommentDto mapToDto(Comment comment) {
        User author = userRepository.findById(comment.getAuthorId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(author.getName());
        dto.setCreated(comment.getCreated());

        return dto;
    }
}
