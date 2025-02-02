package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.RequestedItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Long id;

    private String description;

    private UserDto requestor;

    private LocalDateTime created;

    private List<RequestedItemDto> items;
}
