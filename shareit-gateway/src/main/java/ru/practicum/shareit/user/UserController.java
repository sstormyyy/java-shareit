package ru.practicum.shareit.user;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto dto) { return userClient.create(dto); }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @Valid @RequestBody UserDto dto) { return userClient.update(id, dto); }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) { return userClient.getById(id); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) { return userClient.delete(id); }

    @GetMapping
    public ResponseEntity<Object> getAll() { return userClient.getAll(); }
}
