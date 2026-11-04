package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
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
        User user = new User(null, dto.getName(), dto.getEmail());
        User created = userRepository.save(user);
        return new UserDto(created.getId(), created.getName(), created.getEmail());
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
        return new UserDto(updated.getId(), updated.getName(), updated.getEmail());
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}