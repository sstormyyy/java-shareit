package ru.practicum.shareit.request.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private Integer id;
    @NotBlank
    private String description;
    private LocalDateTime created;
}
