package ru.practicum.shareit.user.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto create(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return mapToDto(userRepository.save(user));
    }

    public UserDto update(Integer id, UserDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) {
            if (userRepository.existsByEmail(dto.getEmail()) && !user.getEmail().equals(dto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(dto.getEmail());
        }
        return mapToDto(userRepository.save(user));
    }

    public UserDto getById(Integer id) {
        return mapToDto(userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found")));
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
