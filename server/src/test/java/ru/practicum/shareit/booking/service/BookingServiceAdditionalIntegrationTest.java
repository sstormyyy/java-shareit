package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
class BookingServiceAdditionalIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createShouldRejectMissingBooker() {
        User owner = createUser("Owner", "owner@test.ru");
        Item item = createItem(owner.getId());

        assertThatThrownBy(() ->
                bookingService.create(999999, createBookingDto(item.getId())))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createShouldRejectMissingItem() {
        User booker = createUser("Booker", "booker@test.ru");

        assertThatThrownBy(() ->
                bookingService.create(booker.getId(), createBookingDto(999999)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createShouldRejectNullStartDate() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        Item item = createItem(owner.getId());

        BookingDto dto = createBookingDto(item.getId());
        dto.setStart(null);

        assertThatThrownBy(() -> bookingService.create(booker.getId(), dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createShouldRejectNullEndDate() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        Item item = createItem(owner.getId());

        BookingDto dto = createBookingDto(item.getId());
        dto.setEnd(null);

        assertThatThrownBy(() -> bookingService.create(booker.getId(), dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createShouldRejectInvalidDateRange() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        Item item = createItem(owner.getId());

        BookingDto dto = createBookingDto(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> bookingService.create(booker.getId(), dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void approveShouldRejectMissingBooking() {
        User owner = createUser("Owner", "owner@test.ru");

        assertThatThrownBy(() ->
                bookingService.approve(999999, owner.getId(), true))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void approveShouldRejectNullApprovedParameter() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        Item item = createItem(owner.getId());
        Booking booking = createBooking(item.getId(), booker.getId());

        assertThatThrownBy(() ->
                bookingService.approve(booking.getId(), owner.getId(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void approveShouldRejectAlreadyProcessedBooking() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        Item item = createItem(owner.getId());
        Booking booking = createBooking(item.getId(), booker.getId());

        booking.setStatus(Booking.BookingStatus.APPROVED);
        bookingRepository.save(booking);

        assertThatThrownBy(() ->
                bookingService.approve(booking.getId(), owner.getId(), true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByIdShouldRejectMissingBooking() {
        assertThatThrownBy(() -> bookingService.getById(999999, 1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByIdShouldAllowItemOwner() {
        User owner = createUser("Owner", "owner@test.ru");
        User booker = createUser("Booker", "booker@test.ru");
        Item item = createItem(owner.getId());
        Booking booking = createBooking(item.getId(), booker.getId());

        BookingDto result = bookingService.getById(booking.getId(), owner.getId());

        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    void getByBookerShouldRejectMissingUser() {
        assertThatThrownBy(() -> bookingService.getByBooker(999999, null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByOwnerShouldRejectMissingUser() {
        assertThatThrownBy(() -> bookingService.getByOwner(999999, null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByOwnerShouldReturnEmptyListWhenOwnerHasNoItems() {
        User owner = createUser("Owner", "owner@test.ru");

        assertThat(bookingService.getByOwner(owner.getId(), null)).isEmpty();
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