package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class ItemCreateDto {

    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "description must not be blank")
    private String description;

    @NotNull(message = "Availability must be specified")
    private Boolean available;

    private Long requestId;
}
