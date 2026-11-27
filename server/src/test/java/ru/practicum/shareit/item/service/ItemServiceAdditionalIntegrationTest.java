package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
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
class ItemServiceAdditionalIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void updateShouldKeepFieldsWhenTheyAreNull() {
        User owner = createUser("Owner", "owner-additional@test.ru");
        Item item = createItem(owner.getId(), "Drill", "Original description");

        ItemDto update = new ItemDto();

        ItemDto result = itemService.update(item.getId(), owner.getId(), update);

        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Original description");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void updateShouldRejectMissingItem() {
        User owner = createUser("Owner", "owner-missing@test.ru");

        assertThatThrownBy(() ->
                itemService.update(999999, owner.getId(), new ItemDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByIdShouldRejectMissingItem() {
        assertThatThrownBy(() -> itemService.getById(999999, null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByIdShouldReturnLastAndNextBookingsForOwner() {
        User owner = createUser("Owner", "owner-bookings@test.ru");
        User booker = createUser("Booker", "booker-bookings@test.ru");
        Item item = createItem(owner.getId(), "Drill", "Test description");

        Booking lastBooking = createBooking(
                item.getId(),
                booker.getId(),
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2)
        );

        Booking nextBooking = createBooking(
                item.getId(),
                booker.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3)
        );

        ItemDto result = itemService.getById(item.getId(), owner.getId());

        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getLastBooking().getId()).isEqualTo(lastBooking.getId());
        assertThat(result.getLastBooking().getBookerId()).isEqualTo(booker.getId());

        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getNextBooking().getId()).isEqualTo(nextBooking.getId());
        assertThat(result.getNextBooking().getBookerId()).isEqualTo(booker.getId());
    }

    @Test
    void searchShouldFindItemByDescription() {
        User owner = createUser("Owner", "owner-description@test.ru");
        createItem(owner.getId(), "Drill", "Powerful electric tool");

        List<ItemDto> result = itemService.search("electric");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void searchShouldReturnEmptyListWhenTextIsNull() {
        assertThat(itemService.search(null)).isEmpty();
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(Integer ownerId, String name, String description) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwnerId(ownerId);
        return itemRepository.save(item);
    }

    private Booking createBooking(
            Integer itemId,
            Integer bookerId,
            LocalDateTime start,
            LocalDateTime end) {

        Booking booking = new Booking();
        booking.setItemId(itemId);
        booking.setBookerId(bookerId);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(Booking.BookingStatus.APPROVED);
        return bookingRepository.save(booking);
    }
}