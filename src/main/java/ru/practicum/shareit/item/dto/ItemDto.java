package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    Long id;

    @NotBlank(message = "Name must not be blank")
    String name;

    @NotBlank(message = "description must not be blank")
    String description;

    @NotNull(message = "Availability must be specified")
    Boolean available;
}
