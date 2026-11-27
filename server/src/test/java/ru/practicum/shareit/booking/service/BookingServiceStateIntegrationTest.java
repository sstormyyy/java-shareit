package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
class BookingServiceStateIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFilterBookingsByEveryStateForBookerAndOwner() {
        User owner = createUser("Owner", "state-owner@test.ru");
        User booker = createUser("Booker", "state-booker@test.ru");
        Item item = createItem(owner.getId());

        Booking past = createBooking(
                item.getId(), booker.getId(),
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(3),
                Booking.BookingStatus.APPROVED
        );

        Booking current = createBooking(
                item.getId(), booker.getId(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                Booking.BookingStatus.APPROVED
        );

        Booking future = createBooking(
                item.getId(), booker.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                Booking.BookingStatus.APPROVED
        );

        Booking waiting = createBooking(
                item.getId(), booker.getId(),
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                Booking.BookingStatus.WAITING
        );

        Booking rejected = createBooking(
                item.getId(), booker.getId(),
                LocalDateTime.now().plusDays(6),
                LocalDateTime.now().plusDays(7),
                Booking.BookingStatus.REJECTED
        );

        assertStateForBothUsers(booker, owner, BookingState.ALL, 5);
        assertStateForBothUsers(booker, owner, BookingState.CURRENT,
                current.getId());
        assertStateForBothUsers(booker, owner, BookingState.PAST,
                past.getId());
        assertStateForBothUsers(booker, owner, BookingState.FUTURE,
                future.getId());
        assertStateForBothUsers(booker, owner, BookingState.WAITING,
                waiting.getId());
        assertStateForBothUsers(booker, owner, BookingState.REJECTED,
                rejected.getId());
    }

    private void assertStateForBothUsers(
            User booker, User owner, BookingState state, Integer expectedId) {

        List<BookingDto> bookerBookings =
                bookingService.getByBooker(booker.getId(), state);

        List<BookingDto> ownerBookings =
                bookingService.getByOwner(owner.getId(), state);

        assertThat(bookerBookings)
                .extracting(BookingDto::getId)
                .containsExactly(expectedId);

        assertThat(ownerBookings)
                .extracting(BookingDto::getId)
                .containsExactly(expectedId);
    }

    private void assertStateForBothUsers(
            User booker, User owner, BookingState state, int expectedSize) {

        assertThat(bookingService.getByBooker(booker.getId(), state))
                .hasSize(expectedSize);

        assertThat(bookingService.getByOwner(owner.getId(), state))
                .hasSize(expectedSize);
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(Integer ownerId) {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwnerId(ownerId);
        return itemRepository.save(item);
    }

    private Booking createBooking(
            Integer itemId,
            Integer bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Booking.BookingStatus status) {

        Booking booking = new Booking();
        booking.setItemId(itemId);
        booking.setBookerId(bookerId);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}