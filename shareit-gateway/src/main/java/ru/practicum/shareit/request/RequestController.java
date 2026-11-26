package ru.practicum.shareit.request;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemRequestDto dto) {
        return requestClient.create(dto, userId.toString());
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestClient.getAllByRequestor(userId.toString());
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOthers(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestClient.getAllOthers(userId.toString());
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Integer requestId) {
        return requestClient.getById(requestId);
    }
}
