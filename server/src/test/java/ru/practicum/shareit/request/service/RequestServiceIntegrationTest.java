package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
class RequestServiceIntegrationTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createShouldSaveRequest() {
        User user = createUser("Requester", "requester@test.ru");

        ItemRequestDto input = new ItemRequestDto();
        input.setDescription("I need a drill");

        ItemRequestDto result = requestService.create(user.getId(), input);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("I need a drill");
        assertThat(result.getCreated()).isNotNull();

        assertThat(requestRepository.findById(result.getId())).isPresent();
    }

    @Test
    void getAllByRequestorShouldReturnRequestsWithItems() {
        User requester = createUser("Requester", "requester@test.ru");
        User owner = createUser("Owner", "owner@test.ru");

        ItemRequest olderRequest = createRequest(
                requester.getId(),
                "Older request",
                LocalDateTime.now().minusDays(2)
        );

        ItemRequest newerRequest = createRequest(
                requester.getId(),
                "Newer request",
                LocalDateTime.now().minusDays(1)
        );

        createItem(owner.getId(), olderRequest.getId(), "Drill");

        List<ItemRequestWithItemsDto> result =
                requestService.getAllByRequestor(requester.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(newerRequest.getId());
        assertThat(result.get(1).getId()).isEqualTo(olderRequest.getId());

        assertThat(result.get(1).getItems()).hasSize(1);
        assertThat(result.get(1).getItems().get(0).getName())
                .isEqualTo("Drill");
        assertThat(result.get(1).getItems().get(0).getOwnerId())
                .isEqualTo(owner.getId());
    }

    @Test
    void getAllOthersShouldReturnOnlyOtherUsersRequests() {
        User requester = createUser("Requester", "requester@test.ru");
        User anotherUser = createUser("Another", "another@test.ru");

        createRequest(
                requester.getId(),
                "My request",
                LocalDateTime.now().minusDays(2)
        );

        ItemRequest anotherRequest = createRequest(
                anotherUser.getId(),
                "Another user's request",
                LocalDateTime.now().minusDays(1)
        );

        List<ItemRequestDto> result =
                requestService.getAllOthers(requester.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(anotherRequest.getId());
        assertThat(result.get(0).getDescription())
                .isEqualTo("Another user's request");
    }

    @Test
    void getByIdShouldReturnRequestWithItems() {
        User requester = createUser("Requester", "requester@test.ru");
        User owner = createUser("Owner", "owner@test.ru");

        ItemRequest request = createRequest(
                requester.getId(),
                "I need a bicycle",
                LocalDateTime.now()
        );

        createItem(owner.getId(), request.getId(), "Bicycle");

        ItemRequestWithItemsDto result =
                requestService.getById(request.getId());

        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getDescription()).isEqualTo("I need a bicycle");

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Bicycle");
        assertThat(result.getItems().get(0).getOwnerId()).isEqualTo(owner.getId());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private ItemRequest createRequest(
            Integer requestorId,
            String description,
            LocalDateTime created
    ) {
        ItemRequest request = new ItemRequest();
        request.setRequestorId(requestorId);
        request.setDescription(description);
        request.setCreated(created);
        return requestRepository.save(request);
    }

    private Item createItem(Integer ownerId, Integer requestId, String name) {
        Item item = new Item();
        item.setOwnerId(ownerId);
        item.setRequestId(requestId);
        item.setName(name);
        item.setDescription("Test description");
        item.setAvailable(true);
        return itemRepository.save(item);
    }
}