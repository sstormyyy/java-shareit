package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ResponseEntity<Object> create(UserDto dto) {
        return post("/users", dto);
    }

    public ResponseEntity<Object> update(int id, UserDto dto) {
        return patch("/users/" + id, dto);
    }

    public ResponseEntity<Object> getById(int id) {
        return get("/users/" + id);
    }

    public ResponseEntity<Object> delete(int id) {
        return delete("/users/" + id);
    }
}