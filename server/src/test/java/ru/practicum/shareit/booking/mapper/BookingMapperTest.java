package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testUser", "test@example.com");
        item = new Item(1L, "testItem", "description", true, user, null);

        booking = new Booking(
                1L,
                LocalDateTime.of(2023, 12, 1, 10, 0),
                LocalDateTime.of(2023, 12, 2, 10, 0),
                item,
                user,
                BookingStatus.APPROVED
        );

        bookingDto = new BookingDto(
                1L,
                new UserDto(1L, "testUser", "test@example.com"),
                LocalDateTime.of(2023, 12, 1, 10, 0),
                LocalDateTime.of(2023, 12, 2, 10, 0),
                new ItemDto(1L, "Test Item", "Description", true, null, null, null, null, null),
                BookingStatus.APPROVED
        );

        bookingCreateDto = new BookingCreateDto(
                1L,
                LocalDateTime.of(2023, 12, 1, 10, 0),
                LocalDateTime.of(2023, 12, 2, 10, 0)
        );
    }

    @Test
    void toDto_shouldMapBookingToBookingDto() {
        BookingDto result = BookingMapper.toDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getBooker().getId(), result.getBooker().getId());
        assertEquals(booking.getStartDate(), result.getStart());
        assertEquals(booking.getEndDate(), result.getEnd());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void toBooking_shouldMapBookingDtoToBooking() {
        Booking result = BookingMapper.toBooking(bookingDto, user, item);

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStart(), result.getStartDate());
        assertEquals(bookingDto.getEnd(), result.getEndDate());
        assertEquals(bookingDto.getItem().getId(), result.getItem().getId());
        assertEquals(bookingDto.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingDto.getStatus(), result.getStatus());
    }

    @Test
    void toBooking_shouldMapBookingCreateDtoToBooking() {
        Booking result = BookingMapper.toBooking(bookingCreateDto, user, item);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(bookingCreateDto.getStart(), result.getStartDate());
        assertEquals(bookingCreateDto.getEnd(), result.getEndDate());
        assertEquals(bookingCreateDto.getItemId(), result.getItem().getId());
        assertEquals(user.getId(), result.getBooker().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }
}
