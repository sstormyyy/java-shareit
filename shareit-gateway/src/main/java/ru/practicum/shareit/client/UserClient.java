package ru.practicum.shareit.client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> create(UserDto dto) { return post("/users", dto); }
    public ResponseEntity<Object> update(Integer id, UserDto dto) { return patch("/users/" + id, dto); }
    public ResponseEntity<Object> getById(Integer id) { return get("/users/" + id, null); }
    public ResponseEntity<Object> delete(Integer id) { return delete("/users/" + id); }
    public ResponseEntity<Object> getAll() { return get("/users", null); }
}
