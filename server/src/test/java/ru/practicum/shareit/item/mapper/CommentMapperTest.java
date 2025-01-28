package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toDtoShouldMapCommentToCommentDto() {
        // Arrange
        User author = new User();
        author.setId(1L);
        author.setName("John Doe");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("This is a test comment.");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        // Act
        CommentDto commentDto = CommentMapper.toDto(comment);

        // Assert
        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}
