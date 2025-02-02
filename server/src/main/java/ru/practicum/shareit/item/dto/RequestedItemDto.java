package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class RequestedItemDto {
    private Long id;

    private String name;

    private Long ownerId;
}
