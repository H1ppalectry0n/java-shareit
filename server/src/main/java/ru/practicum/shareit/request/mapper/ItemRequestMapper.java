package ru.practicum.shareit.request.mapper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(@Nonnull ItemRequest itemRequest, @Nullable List<Item> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                items != null ? items.stream().map(ItemMapper::toRequestedItemDto).toList() : null
        );
    }
}
