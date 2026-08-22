package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public UserDto create(UserDto dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (!dto.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email должен содержать @");
        }
        if (storage.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }
        User user = UserMapper.toUser(dto);
        User created = storage.create(user);
        return UserMapper.toUserDto(created);
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        User existing = storage.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getEmail() != null) {
            if (!dto.getEmail().equalsIgnoreCase(existing.getEmail())
                    && storage.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email уже используется");
            }
            existing.setEmail(dto.getEmail());
        }
        User updated = storage.update(existing);
        return UserMapper.toUserDto(updated);
    }

    @Override
    public UserDto findById(Long id) {
        User user = storage.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return storage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        storage.deleteById(id);
    }
}