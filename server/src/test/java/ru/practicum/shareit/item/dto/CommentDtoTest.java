package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialization() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Test text", "Author", LocalDateTime.of(2023, 9, 16, 12, 0));

        String jsonContent = json.write(commentDto).getJson();

        assertThat(jsonContent).contains("\"id\":1");
        assertThat(jsonContent).contains("\"text\":\"Test text\"");
        assertThat(jsonContent).contains("\"authorName\":\"Author\"");
        assertThat(jsonContent).contains("\"created\":\"2023-09-16T12:00:00\"");
    }

    @Test
    void testDeserialization() throws Exception {
        String jsonContent = "{\"id\":1,\"text\":\"Test text\",\"authorName\":\"Author\",\"created\":\"2023-09-16T12:00:00\"}";

        CommentDto commentDto = json.parse(jsonContent).getObject();

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("Test text");
        assertThat(commentDto.getAuthorName()).isEqualTo("Author");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2023, 9, 16, 12, 0));
    }
}