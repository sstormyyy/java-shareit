package ru.practicum.shareit.user.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto dto) { return userService.create(dto); }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Integer id, @RequestBody UserDto dto) { return userService.update(id, dto); }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Integer id) { return userService.getById(id); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { userService.delete(id); }

    @GetMapping
    public List<UserDto> getAll() { return userService.getAll(); }
}
