package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class DtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestJson;

    @Autowired
    private JacksonTester<BookingDto> bookingJson;

    @Test
    void shouldSerializeItemRequestCreatedAsIsoDateTime() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setCreated(LocalDateTime.of(2026, 11, 27, 12, 30));

        assertThat(itemRequestJson.write(dto))
                .extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-11-27T12:30:00");
    }

    @Test
    void shouldSerializeBookingDatesAsIsoDateTime() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.of(2026, 11, 27, 12, 30));
        dto.setEnd(LocalDateTime.of(2026, 11, 28, 14, 0));

        assertThat(bookingJson.write(dto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-11-27T12:30:00");

        assertThat(bookingJson.write(dto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-11-28T14:00:00");
    }
}