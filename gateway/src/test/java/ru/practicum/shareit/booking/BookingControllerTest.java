package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.exception.ErrorHandler;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import(ErrorHandler.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void createShouldCallClient() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setItemId(10);
        dto.setStart(LocalDateTime.now().plusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(3));

        when(bookingClient.create(eq(1), any(BookingDto.class)))
                .thenReturn(ResponseEntity.ok((Object) "created"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(bookingClient).create(eq(1), any(BookingDto.class));
    }

    @Test
    void createShouldRejectInvalidDto() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content("{\"itemId\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveShouldCallClient() throws Exception {
        when(bookingClient.approve(1, 5, true))
                .thenReturn(ResponseEntity.ok((Object) "approved"));

        mockMvc.perform(patch("/bookings/5")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approve(1, 5, true);
    }

    @Test
    void getByIdShouldCallClient() throws Exception {
        when(bookingClient.getById(1, 5))
                .thenReturn(ResponseEntity.ok((Object) "booking"));

        mockMvc.perform(get("/bookings/5")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).getById(1, 5);
    }

    @Test
    void getAllShouldUseDefaultState() throws Exception {
        when(bookingClient.getAll(1, "ALL"))
                .thenReturn(ResponseEntity.ok((Object) "bookings"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).getAll(1, "ALL");
    }

    @Test
    void getAllForOwnerShouldPassRequestedState() throws Exception {
        when(bookingClient.getAllForOwner(1, "FUTURE"))
                .thenReturn(ResponseEntity.ok((Object) "bookings"));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk());

        verify(bookingClient).getAllForOwner(1, "FUTURE");
    }
}