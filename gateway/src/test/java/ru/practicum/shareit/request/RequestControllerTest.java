package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.RequestClient;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
@Import(ErrorHandler.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    @Test
    void createShouldCallClient() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Нужна дрель");

        when(requestClient.create(eq(1), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok((Object) "created"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(requestClient).create(eq(1), any(ItemRequestDto.class));
    }

    @Test
    void createShouldRejectInvalidDto() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllShouldCallClient() throws Exception {
        when(requestClient.getAll(1))
                .thenReturn(ResponseEntity.ok((Object) "requests"));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getAll(1);
    }

    @Test
    void getAllOtherShouldCallClient() throws Exception {
        when(requestClient.getAllOther(1))
                .thenReturn(ResponseEntity.ok((Object) "requests"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getAllOther(1);
    }

    @Test
    void getByIdShouldCallClient() throws Exception {
        when(requestClient.getById(5))
                .thenReturn(ResponseEntity.ok((Object) "request"));

        mockMvc.perform(get("/requests/5"))
                .andExpect(status().isOk());

        verify(requestClient).getById(5);
    }
}