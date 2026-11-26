package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return get(path, parameters, null);
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters,
                                         @Nullable Map<String, String> headers) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null, headers);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, body, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body, @Nullable Map<String, String> headers) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body, headers);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, body, null, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body, @Nullable Map<String, String> headers) {
        return patch(path, body, null, headers);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body,
                                               @Nullable Map<String, Object> parameters,
                                               @Nullable Map<String, String> headers) {
        return makeAndSendRequest(HttpMethod.PATCH, path, parameters, body, headers);
    }

    protected ResponseEntity<Object> delete(String path) {
        return makeAndSendRequest(HttpMethod.DELETE, path, null, null, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                           @Nullable Map<String, Object> parameters,
                                                           @Nullable T body,
                                                           @Nullable Map<String, String> headers) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(headers));
        ResponseEntity<Object> serverResponse;
        try {
            if (parameters != null && !parameters.isEmpty()) {
                serverResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                serverResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            log.error("{} {} server error: {}", method, path, e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(serverResponse);
    }

    private HttpHeaders defaultHeaders(@Nullable Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        return httpHeaders;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}