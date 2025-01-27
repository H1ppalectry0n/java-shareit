package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoTest {
    private final JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws IOException {
        ItemDto itemDto = new ItemDto(
                1L,
                "Item Name",
                "Item Description",
                true,
                null,
                null,
                null,
                null,
                null
        );

        // when
        var jsonContent = json.write(itemDto).getJson();

        assertThat(jsonContent).containsPattern("id\":1");
        assertThat(jsonContent).containsPattern("\"name\":\"Item Name\"");
        assertThat(jsonContent).containsPattern("\"description\":\"Item Description\"");
        assertThat(jsonContent).containsPattern("\"available\":true");

    }
}