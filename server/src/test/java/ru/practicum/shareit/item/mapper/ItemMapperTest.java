package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.RequestedItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void testToRequestItemDto() {
        Item item = new Item(
                1L,
                "Test item",
                "Test description",
                true,
                new User(
                        2L,
                        "User name",
                        "User email"
                ),
                null
        );

        RequestedItemDto requestedItemDto = ItemMapper.toRequestedItemDto(item);

        assertThat(requestedItemDto.getId(), equalTo(item.getId()));
        assertThat(requestedItemDto.getName(), equalTo(item.getName()));
        assertThat(requestedItemDto.getOwnerId(), equalTo(item.getOwner().getId()));
    }
}