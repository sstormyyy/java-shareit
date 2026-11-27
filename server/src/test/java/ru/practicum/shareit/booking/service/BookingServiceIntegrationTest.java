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
import ru.practicum.shareit.exception.NotFoundException;
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
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createShouldSaveBookingWithWaitingStatus() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        Item item = createItem(owner.getId(), "Drill", true);

        BookingDto result = bookingService.create(
                booker.getId(),
                createBookingDto(item.getId())
        );

        assertThat(result.getId()).isNotNull();
        assertThat(result.getItemId()).isEqualTo(item.getId());
        assertThat(result.getStatus()).isEqualTo("WAITING");
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void createShouldRejectBookingOfOwnItem() {
        User owner = createUser("Owner", "owner@test.ru");
        Item item = createItem(owner.getId(), "Drill", true);

        assertThatThrownBy(() ->
                bookingService.create(owner.getId(), createBookingDto(item.getId())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createShouldRejectUnavailableItem() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        Item item = createItem(owner.getId(), "Drill", false);

        assertThatThrownBy(() ->
                bookingService.create(booker.getId(), createBookingDto(item.getId())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void approveShouldChangeBookingStatus() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        Item item = createItem(owner.getId(), "Drill", true);
        Booking booking = createBooking(item.getId(), booker.getId());

        BookingDto result = bookingService.approve(
                booking.getId(),
                owner.getId(),
                true
        );

        assertThat(result.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void approveShouldRejectAnotherUsersRequest() {
        User owner = createUser("Owner", "owner@test.ru");
        User anotherUser = createUser("Another", "another@test.ru");
        User booker = createUser("Booker", "booker@test.ru");

        Item item = createItem(owner.getId(), "Drill", true);
        Booking booking = createBooking(item.getId(), booker.getId());

        assertThatThrownBy(() ->
                bookingService.approve(booking.getId(), anotherUser.getId(), true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByIdShouldReturnBookingForBooker() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        Item item = createItem(owner.getId(), "Drill", true);
        Booking booking = createBooking(item.getId(), booker.getId());

        BookingDto result = bookingService.getById(
                booking.getId(),
                booker.getId()
        );

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getItem().getName()).isEqualTo("Drill");
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void getByIdShouldRejectUnrelatedUser() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        User anotherUser = createUser("Another", "another@test.ru");

        Item item = createItem(owner.getId(), "Drill", true);
        Booking booking = createBooking(item.getId(), booker.getId());

        assertThatThrownBy(() ->
                bookingService.getById(booking.getId(), anotherUser.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByBookerAndGetByOwnerShouldReturnTheirBookings() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        User anotherUser = createUser("Another", "another@test.ru");

        Item item = createItem(owner.getId(), "Drill", true);
        Item anotherItem = createItem(anotherUser.getId(), "Bicycle", true);

        createBooking(item.getId(), booker.getId());
        createBooking(anotherItem.getId(), booker.getId());

        List<BookingDto> bookerBookings =
                bookingService.getByBooker(booker.getId(), BookingState.ALL);

        List<BookingDto> ownerBookings =
                bookingService.getByOwner(owner.getId(), BookingState.ALL);

        assertThat(bookerBookings).hasSize(2);
        assertThat(ownerBookings).hasSize(1);
        assertThat(ownerBookings.get(0).getItemId()).isEqualTo(item.getId());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(Integer ownerId, String name, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription("Test description");
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        return itemRepository.save(item);
    }

    private Booking createBooking(Integer itemId, Integer bookerId) {
        Booking booking = new Booking();
        booking.setItemId(itemId);
        booking.setBookerId(bookerId);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Booking.BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    private BookingDto createBookingDto(Integer itemId) {
        BookingDto dto = new BookingDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }
}