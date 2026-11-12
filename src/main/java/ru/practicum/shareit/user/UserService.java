package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);
    UserDto update(UserDto userDto);
    UserDto getById(Long id);
    User getUserById(Long id);
    List<UserDto> getAll();
    void deleteById(Long id);
}