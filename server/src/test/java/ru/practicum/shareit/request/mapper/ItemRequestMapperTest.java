package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    @Test
    void toDto() {
        List<Item> itemDtoList = List.of(new Item(
                3L,
                "Test item",
                "Test Description",
                true,
                new User(
                        4L,
                        "User name 2",
                        "User email 2"
                ),
                null
        ));

        ItemRequest itemRequest = new ItemRequest(
                1L,
                "Test description",
                new User(
                        2L,
                        "User name",
                        "User email"
                ),
                LocalDateTime.now(),
                itemDtoList
        );


        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest, itemRequest.getItems());

        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDto.getRequestor()).isEqualTo(UserMapper.toDto(itemRequest.getRequestor()));
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(itemRequestDto.getItems()).hasSize(1);
    }
}