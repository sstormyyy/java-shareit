package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createShouldSaveUser() {
        UserDto result = userService.create(createUserDto("Иван", "ivan@test.ru"));

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Иван");
        assertThat(result.getEmail()).isEqualTo("ivan@test.ru");
    }

    @Test
    void createShouldRejectDuplicateEmail() {
        userService.create(createUserDto("Иван", "ivan@test.ru"));

        assertThatThrownBy(() ->
                userService.create(createUserDto("Пётр", "ivan@test.ru"))
        ).isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void updateShouldChangeUserNameAndEmail() {
        UserDto created = userService.create(
                createUserDto("Иван", "ivan@test.ru")
        );

        UserDto updateDto = new UserDto();
        updateDto.setName("Пётр");
        updateDto.setEmail("petr@test.ru");

        UserDto result = userService.update(created.getId(), updateDto);

        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getName()).isEqualTo("Пётр");
        assertThat(result.getEmail()).isEqualTo("petr@test.ru");
    }

    @Test
    void updateShouldRejectAnotherUsersEmail() {
        UserDto first = userService.create(
                createUserDto("Иван", "ivan@test.ru")
        );
        userService.create(createUserDto("Пётр", "petr@test.ru"));

        UserDto updateDto = new UserDto();
        updateDto.setEmail("petr@test.ru");

        assertThatThrownBy(() ->
                userService.update(first.getId(), updateDto)
        ).isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void getByIdShouldReturnUser() {
        UserDto created = userService.create(
                createUserDto("Иван", "ivan@test.ru")
        );

        UserDto result = userService.getById(created.getId());

        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getName()).isEqualTo("Иван");
        assertThat(result.getEmail()).isEqualTo("ivan@test.ru");
    }

    @Test
    void getByIdShouldRejectUnknownUser() {
        assertThatThrownBy(() -> userService.getById(999999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteShouldRemoveUser() {
        UserDto created = userService.create(
                createUserDto("Иван", "ivan@test.ru")
        );

        userService.delete(created.getId());

        assertThat(userRepository.existsById(created.getId())).isFalse();
    }

    @Test
    void getAllShouldReturnAllUsers() {
        userService.create(createUserDto("Иван", "ivan@test.ru"));
        userService.create(createUserDto("Пётр", "petr@test.ru"));

        List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(UserDto::getEmail)
                .containsExactlyInAnyOrder("ivan@test.ru", "petr@test.ru");
    }

    private UserDto createUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}