package ru.practicum.shareit.item.dto;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

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

    User owner;

    ItemRequest request;

    LocalDateTime lastBooking;
    LocalDateTime nextBooking;

    List<CommentDto> comments;
}
