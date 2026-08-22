package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto create(UserDto dto);

    UserDto update(Long id, UserDto dto);

    UserDto findById(Long id);

    List<UserDto> findAll();

    void deleteById(Long id);
}