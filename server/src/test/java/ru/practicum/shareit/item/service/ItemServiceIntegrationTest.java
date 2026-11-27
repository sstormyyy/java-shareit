package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    void createShouldSaveItemWithoutRequest() {
        User owner = createUser("Owner", "owner@test.ru");

        ItemDto input = createItemDto("Drill", null);

        ItemDto result = itemService.create(owner.getId(), input);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getOwnerId()).isEqualTo(owner.getId());
        assertThat(result.getRequestId()).isNull();
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void createShouldSaveItemWithRequest() {
        User owner = createUser("Owner", "owner@test.ru");
        User requester = createUser("Requester", "requester@test.ru");
        ItemRequest request = createRequest(requester.getId());

        ItemDto input = createItemDto("Bicycle", request.getId());

        ItemDto result = itemService.create(owner.getId(), input);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void createShouldThrowExceptionWhenRequestDoesNotExist() {
        User owner = createUser("Owner", "owner@test.ru");

        ItemDto input = createItemDto("Bicycle", 999999);

        assertThatThrownBy(() -> itemService.create(owner.getId(), input))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateShouldChangeItemFields() {
        User owner = createUser("Owner", "owner@test.ru");
        Item item = createItem(owner.getId(), "Old name");

        ItemDto update = new ItemDto();
        update.setName("New name");
        update.setDescription("New description");
        update.setAvailable(false);

        ItemDto result = itemService.update(item.getId(), owner.getId(), update);

        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getDescription()).isEqualTo("New description");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void updateShouldRejectAnotherUsersItem() {
        User owner = createUser("Owner", "owner@test.ru");
        User anotherUser = createUser("Another", "another@test.ru");
        Item item = createItem(owner.getId(), "Drill");

        ItemDto update = new ItemDto();
        update.setName("Changed name");

        assertThatThrownBy(() ->
                itemService.update(item.getId(), anotherUser.getId(), update))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByIdShouldReturnItem() {
        User owner = createUser("Owner", "owner@test.ru");
        Item item = createItem(owner.getId(), "Drill");

        ItemDto result = itemService.getById(item.getId(), null);

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void getByOwnerShouldReturnOnlyOwnersItems() {
        User owner = createUser("Owner", "owner@test.ru");
        User anotherUser = createUser("Another", "another@test.ru");

        createItem(owner.getId(), "Drill");
        createItem(owner.getId(), "Bicycle");
        createItem(anotherUser.getId(), "Camera");

        List<ItemDto> result = itemService.getByOwner(owner.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ItemDto::getName)
                .containsExactlyInAnyOrder("Drill", "Bicycle");
    }

    @Test
    void searchShouldReturnOnlyAvailableMatchingItems() {
        User owner = createUser("Owner", "owner@test.ru");

        createItem(owner.getId(), "Blue bicycle", true);
        createItem(owner.getId(), "Red bicycle", false);
        createItem(owner.getId(), "Camera", true);

        List<ItemDto> result = itemService.search("bicycle");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Blue bicycle");
    }

    @Test
    void searchShouldReturnEmptyListForBlankText() {
        assertThat(itemService.search("   ")).isEmpty();
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(Integer ownerId, String name) {
        return createItem(ownerId, name, true);
    }

    private Item createItem(Integer ownerId, String name, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription("Test description");
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        return itemRepository.save(item);
    }

    private ItemRequest createRequest(Integer requestorId) {
        ItemRequest request = new ItemRequest();
        request.setRequestorId(requestorId);
        request.setDescription("Need a bicycle");
        request.setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }

    private ItemDto createItemDto(String name, Integer requestId) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription("Test description");
        dto.setAvailable(true);
        dto.setRequestId(requestId);
        return dto;
    }
}