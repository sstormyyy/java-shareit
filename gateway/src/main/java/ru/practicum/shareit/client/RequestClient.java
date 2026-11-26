package ru.practicum.shareit.client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Service
public class RequestClient extends BaseClient {
    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> create(ItemRequestDto dto, String userIdHeader) { return postWithHeader("/requests", dto, "X-Sharer-User-Id", userIdHeader); }
    public ResponseEntity<Object> getAllByRequestor(String userIdHeader) { return getWithHeader("/requests", "X-Sharer-User-Id", userIdHeader); }
    public ResponseEntity<Object> getAllOthers(String userIdHeader) { return getWithHeader("/requests/all", "X-Sharer-User-Id", userIdHeader); }
    public ResponseEntity<Object> getById(Integer id) { return get("/requests/" + id, null); }

    private <T> ResponseEntity<Object> postWithHeader(String path, T body, String headerName, String headerValue) {
        return makeAndSendRequestWithHeader(HttpMethod.POST, path, null, body, headerName, headerValue);
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
