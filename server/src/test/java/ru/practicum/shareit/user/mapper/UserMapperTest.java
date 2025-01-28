package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

class UserMapperTest {

    @Test
    void testToDto() {
        // Arrange
        User user = new User(1L, "John Doe", "john.doe@example.com");

        // Act
        UserDto userDto = UserMapper.toDto(user);

        // Assert
        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void testToUser() {
        // Arrange
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");

        // Act
        User user = UserMapper.toUser(userDto);

        // Assert
        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }
}
