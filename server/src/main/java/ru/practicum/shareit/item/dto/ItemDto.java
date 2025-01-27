package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private UserDto owner;

    private ItemRequestDto request;

    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;

    private List<CommentDto> comments;
}
