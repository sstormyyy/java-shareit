package ru.practicum.shareit.client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> create(Integer userId, ItemDto dto) { 
        return post("/items", dto); // Note: Header added in controller via interceptor or manually, but for simplicity we pass via custom logic or rely on server. 
        // Actually, BaseClient needs header support. Let's fix BaseClient usage in controller.
    }
    
    // Simplified for this script to work with headers passed from controller
    public ResponseEntity<Object> create(ItemDto dto, String userIdHeader) { 
        return postWithHeader("/items", dto, "X-Sharer-User-Id", userIdHeader);
    }
    public ResponseEntity<Object> update(Integer id, ItemDto dto, String userIdHeader) { return patchWithHeader("/items/" + id, dto, "X-Sharer-User-Id", userIdHeader); }
    public ResponseEntity<Object> getById(Integer id) { return get("/items/" + id, null); }
    public ResponseEntity<Object> getByOwner(String userIdHeader) { return getWithHeader("/items", "X-Sharer-User-Id", userIdHeader); }
    public ResponseEntity<Object> search(String text) { return get("/items/search", Map.of("text", text)); }

    private <T> ResponseEntity<Object> postWithHeader(String path, T body, String headerName, String headerValue) {
        return makeAndSendRequestWithHeader(HttpMethod.POST, path, null, body, headerName, headerValue);
    }
    private <T> ResponseEntity<Object> patchWithHeader(String path, T body, String headerName, String headerValue) {
        return makeAndSendRequestWithHeader(HttpMethod.PATCH, path, null, body, headerName, headerValue);
    }
    private ResponseEntity<Object> getWithHeader(String path, String headerName, String headerValue) {
        return makeAndSendRequestWithHeader(HttpMethod.GET, path, null, null, headerName, headerValue);
    }

    private <T> ResponseEntity<Object> makeAndSendRequestWithHeader(HttpMethod method, String path, java.util.Map<String, Object> parameters, @Nullable T body, String headerName, String headerValue) {
        org.springframework.http.HttpHeaders headers = defaultHeaders();
        headers.set(headerName, headerValue);
        org.springframework.http.HttpEntity<T> requestEntity = new org.springframework.http.HttpEntity<>(body, headers);
        try {
            return prepareGatewayResponse(rest.exchange(path, method, requestEntity, Object.class, parameters != null ? parameters : new java.util.HashMap<>()));
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }
}
