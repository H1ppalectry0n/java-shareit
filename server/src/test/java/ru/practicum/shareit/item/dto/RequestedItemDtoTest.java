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
class RequestedItemDtoTest {
    private final JacksonTester<RequestedItemDto> json;

    @Test
    void testItemDto() throws IOException {
        RequestedItemDto requestedItemDto = new RequestedItemDto(
                1L,
                "Test name",
                4L
        );

        // when
        var jsonContent = json.write(requestedItemDto).getJson();

        assertThat(jsonContent).containsPattern("id\":1");
        assertThat(jsonContent).containsPattern("\"name\":\"Test name\"");
        assertThat(jsonContent).containsPattern("ownerId\":4");

    }

}