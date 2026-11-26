package ru.practicum.shareit.client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> create(BookingDto dto, String userIdHeader) { return postWithHeader("/bookings", dto, "X-Sharer-User-Id", userIdHeader); }
    public ResponseEntity<Object> approve(Integer id, Boolean approved, String userIdHeader) { return patchWithHeader("/bookings/" + id + "?approved=" + approved, null, "X-Sharer-User-Id", userIdHeader); }
    public ResponseEntity<Object> getById(Integer id, String userIdHeader) { return getWithHeader("/bookings/" + id, "X-Sharer-User-Id", userIdHeader); }
    public ResponseEntity<Object> getByBooker(String userIdHeader, String state) { return getWithHeader("/bookings?state=" + state, "X-Sharer-User-Id", userIdHeader); }
    public ResponseEntity<Object> getByOwner(String userIdHeader, String state) { return getWithHeader("/bookings/owner?state=" + state, "X-Sharer-User-Id", userIdHeader); }

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
