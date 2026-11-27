package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createDelegatesToService() {
        UserDto dto = new UserDto();
        when(userService.create(dto)).thenReturn(dto);

        assertSame(dto, userController.create(dto));

        verify(userService).create(dto);
    }

    @Test
    void updateDelegatesToService() {
        UserDto dto = new UserDto();
        when(userService.update(1, dto)).thenReturn(dto);

        assertSame(dto, userController.update(1, dto));

        verify(userService).update(1, dto);
    }

    @Test
    void getByIdDelegatesToService() {
        UserDto dto = new UserDto();
        when(userService.getById(1)).thenReturn(dto);

        assertSame(dto, userController.getById(1));

        verify(userService).getById(1);
    }

    @Test
    void deleteDelegatesToService() {
        userController.delete(1);

        verify(userService).delete(1);
    }

    @Test
    void getAllDelegatesToService() {
        List<UserDto> users = List.of(new UserDto());
        when(userService.getAll()).thenReturn(users);

        assertSame(users, userController.getAll());

        verify(userService).getAll();
    }
}