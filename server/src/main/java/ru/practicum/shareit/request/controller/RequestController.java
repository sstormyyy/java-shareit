package ru.practicum.shareit.request.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.RequestService;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader("X-Sharer-User-Id") Integer requestorId,
            @RequestBody ItemRequestDto dto) {
        return requestService.create(requestorId, dto);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Integer requestorId) {
        return requestService.getAllByRequestor(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllOthers(@RequestHeader("X-Sharer-User-Id") Integer requestorId) {
        return requestService.getAllOthers(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getById(@PathVariable Integer requestId) {
        return requestService.getById(requestId);
    }
}
