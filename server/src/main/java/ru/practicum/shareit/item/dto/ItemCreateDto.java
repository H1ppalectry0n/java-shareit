package ru.practicum.shareit.item.dto;

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

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
