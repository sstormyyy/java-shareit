package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createShouldSaveCommentAfterCompletedBooking() {
        User author = createUser("Автор");
        User owner = createUser("Владелец");
        Item item = createItem(owner.getId());

        createCompletedBooking(item.getId(), author.getId());

        CommentDto result = commentService.create(
                author.getId(), item.getId(), "Отличная вещь"
        );

        assertThat(result.getId()).isNotNull();
        assertThat(result.getText()).isEqualTo("Отличная вещь");
        assertThat(result.getAuthorName()).isEqualTo("Автор");
        assertThat(result.getCreated()).isNotNull();

        assertThat(commentRepository.findAll()).hasSize(1);
    }

    @Test
    void createShouldRejectUnknownUser() {
        User owner = createUser("Владелец");
        Item item = createItem(owner.getId());

        assertThatThrownBy(() ->
                commentService.create(999999, item.getId(), "Комментарий")
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void createShouldRejectUnknownItem() {
        User author = createUser("Автор");

        assertThatThrownBy(() ->
                commentService.create(author.getId(), 999999, "Комментарий")
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void createShouldRejectUserWithoutCompletedBooking() {
        User author = createUser("Автор");
        User owner = createUser("Владелец");
        Item item = createItem(owner.getId());

        assertThatThrownBy(() ->
                commentService.create(author.getId(), item.getId(), "Комментарий")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByItemIdShouldReturnCommentsNewestFirst() {
        User author = createUser("Автор");
        User owner = createUser("Владелец");
        Item item = createItem(owner.getId());

        Comment older = createComment(item.getId(), author.getId(),
                "Старый комментарий", LocalDateTime.now().minusHours(2));
        Comment newer = createComment(item.getId(), author.getId(),
                "Новый комментарий", LocalDateTime.now().minusHours(1));

        List<CommentDto> result = commentService.getByItemId(item.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getText()).isEqualTo("Новый комментарий");
        assertThat(result.get(1).getText()).isEqualTo("Старый комментарий");
    }

    private User createUser(String name) {
        User user = new User();
        user.setName(name);
        user.setEmail(name + System.nanoTime() + "@test.ru");
        return userRepository.save(user);
    }

    private Item createItem(Integer ownerId) {
        Item item = new Item();
        item.setName("Тестовая вещь");
        item.setDescription("Описание тестовой вещи");
        item.setAvailable(true);
        item.setOwnerId(ownerId);
        return itemRepository.save(item);
    }

    private Booking createCompletedBooking(Integer itemId, Integer bookerId) {
        Booking booking = new Booking();
        booking.setItemId(itemId);
        booking.setBookerId(bookerId);
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        booking.setStatus(Booking.BookingStatus.APPROVED);
        return bookingRepository.save(booking);
    }

    private Comment createComment(Integer itemId, Integer authorId,
                                  String text, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setAuthorId(authorId);
        comment.setText(text);
        comment.setCreated(created);
        return commentRepository.save(comment);
    }
}