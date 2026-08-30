package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto create(UserDto dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (!dto.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email должен содержать @");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }
        User user = UserMapper.toUser(dto);
        User created = userRepository.save(user);
        return UserMapper.toUserDto(created);
    }

    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            existing.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!dto.getEmail().equalsIgnoreCase(existing.getEmail())
                    && userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email уже используется");
            }
            existing.setEmail(dto.getEmail());
        }

        User updated = userRepository.save(existing);
        return UserMapper.toUserDto(updated);
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}