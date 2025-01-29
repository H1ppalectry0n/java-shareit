package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBookingDtoSerialization() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1L,
                new UserDto(1L, "Test User", "test@example.com"),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                new ItemDto(1L, "Test Item", "Description", true, null, null, null, null, null),
                BookingStatus.APPROVED
        );

        String json = objectMapper.writeValueAsString(bookingDto);
        assertThat(json).contains("id", "booker", "start", "end", "item", "status");
    }

    @Test
    void testBookingDtoDeserialization() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"booker\": {\"id\": 1, \"name\": \"Test User\", \"email\": \"test@example.com\"}," +
                "\"start\": \"" + LocalDateTime.now().plusDays(1).toString() + "\"," +
                "\"end\": \"" + LocalDateTime.now().plusDays(2).toString() + "\"," +
                "\"item\": {\"id\": 1, \"name\": \"Test Item\", \"description\": \"Description\", \"available\": true}," +
                "\"status\": \"APPROVED\"}";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("Test User");
        assertThat(bookingDto.getItem().getName()).isEqualTo("Test Item");
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}
