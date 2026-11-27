
package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDto {

    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private Integer requestId;

    private BookingInfo lastBooking;

    private BookingInfo nextBooking;

    private List<CommentDto> comments;

    @Data
    public static class BookingInfo {
        private Integer id;
        private Integer bookerId;
    }

    @Data
    public static class CommentDto {
        private Integer id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }
}
