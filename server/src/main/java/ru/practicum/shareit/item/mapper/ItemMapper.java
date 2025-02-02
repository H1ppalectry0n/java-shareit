package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestedItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getIsAvailable());
        dto.setOwner(UserMapper.toDto(item.getOwner()));
        dto.setRequest(item.getRequest() != null ? ItemRequestMapper.toDto(item.getRequest(), null) : null);
        return dto;
    }

    public static Item toItem(ItemCreateDto dto, User owner, ItemRequest request) {
        return new Item(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                owner,
                request
        );
    }

    public static RequestedItemDto toRequestedItemDto(Item item) {
        return new RequestedItemDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }

}
