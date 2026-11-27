package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ResponseEntity<Object> create(int userId, ItemRequestDto dto) {
        return post("/requests", dto, Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> getAll(int userId) {
        return get("/requests", null, Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> getAllOther(int userId) {
        return get("/requests/all", null, Map.of("X-Sharer-User-Id", String.valueOf(userId)));
    }

    public ResponseEntity<Object> getById(int requestId) {
        return get("/requests/" + requestId);
    }
}