package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(ErrorHandler.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void createShouldCallClient() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Электрическая дрель");
        dto.setAvailable(true);

        when(itemClient.create(eq(1), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok((Object) "created"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient).create(eq(1), any(ItemDto.class));
    }

    @Test
    void createShouldRejectInvalidDto() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setName("");
        dto.setDescription("");
        dto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldCallClient() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setName("Новое название");

        when(itemClient.update(eq(1), eq(5), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok((Object) "updated"));

        mockMvc.perform(patch("/items/5")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient).update(eq(1), eq(5), any(ItemDto.class));
    }

    @Test
    void getByIdShouldCallClient() throws Exception {
        when(itemClient.getById(1, 5))
                .thenReturn(ResponseEntity.ok((Object) "item"));

        mockMvc.perform(get("/items/5")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).getById(1, 5);
    }

    @Test
    void getByOwnerShouldCallClient() throws Exception {
        when(itemClient.getByOwner(1))
                .thenReturn(ResponseEntity.ok((Object) "items"));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).getByOwner(1);
    }

    @Test
    void searchShouldCallClient() throws Exception {
        when(itemClient.search("дрель"))
                .thenReturn(ResponseEntity.ok((Object) "items"));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk());

        verify(itemClient).search("дрель");
    }

    @Test
    void createCommentShouldCallClient() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setText("Отличная вещь!");

        when(itemClient.createComment(eq(1), eq(5), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok((Object) "comment"));

        mockMvc.perform(post("/items/5/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient).createComment(eq(1), eq(5), any(CommentDto.class));
    }
}