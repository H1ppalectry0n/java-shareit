package ru.practicum.shareit.request.mapper;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(@NotNull ItemRequest itemRequest, @Nullable List<Item> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                items != null ? items.stream().map(ItemMapper::toRequestedItemDto).toList() : null
        );
    }
}
