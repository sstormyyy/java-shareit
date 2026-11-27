package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ErrorHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void createShouldCallClient() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        doReturn(ResponseEntity.ok().build())
                .when(userClient).create(any(UserDto.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient).create(any(UserDto.class));
    }

    @Test
    void createShouldRejectInvalidDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("");
        userDto.setEmail("not-an-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldCallClient() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Updated User");

        doReturn(ResponseEntity.ok().build())
                .when(userClient).update(eq(5), any(UserDto.class));

        mockMvc.perform(patch("/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient).update(eq(5), any(UserDto.class));
    }

    @Test
    void getByIdShouldCallClient() throws Exception {
        doReturn(ResponseEntity.ok().build())
                .when(userClient).getById(5);

        mockMvc.perform(get("/users/5"))
                .andExpect(status().isOk());

        verify(userClient).getById(5);
    }

    @Test
    void deleteShouldCallClient() throws Exception {
        doReturn(ResponseEntity.ok().build())
                .when(userClient).delete(5);

        mockMvc.perform(delete("/users/5"))
                .andExpect(status().isOk());

        verify(userClient).delete(5);
    }
}