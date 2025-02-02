package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        long userId = 1L;
        BookingCreateDto createDto = new BookingCreateDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto bookingDto = new BookingDto(1L, new UserDto(1L, "User1", "user1@example.com"), createDto.getStart(), createDto.getEnd(), new ItemDto(1L, "Item1", "Description", true, null, null, null, null, List.of()), null);

        when(bookingService.create(eq(userId), any())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":1,\"start\":\"" + createDto.getStart() + "\",\"end\":\"" + createDto.getEnd() + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.name", is("Item1")));

        verify(bookingService, times(1)).create(eq(userId), any());
    }

    @Test
    void approveBooking_shouldReturnUpdatedBooking() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        BookingDto bookingDto = new BookingDto(1L, new UserDto(1L, "User1", "user1@example.com"), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ItemDto(1L, "Item1", "Description", true, null, null, null, null, List.of()), null);

        when(bookingService.approved(eq(userId), eq(bookingId), eq(approved))).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.name", is("Item1")));

        verify(bookingService, times(1)).approved(eq(userId), eq(bookingId), eq(approved));
    }

    @Test
    void getBookingStatus_shouldReturnBooking() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(1L, new UserDto(1L, "User1", "user1@example.com"), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ItemDto(1L, "Item1", "Description", true, null, null, null, null, List.of()), null);

        when(bookingService.bookingStatus(eq(userId), eq(bookingId))).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.name", is("Item1")));

        verify(bookingService, times(1)).bookingStatus(eq(userId), eq(bookingId));
    }

    @Test
    void getBookingsByState_shouldReturnListOfBookings() throws Exception {
        long userId = 1L;
        BookingState state = BookingState.ALL;
        BookingDto bookingDto = new BookingDto(1L, new UserDto(1L, "User1", "user1@example.com"), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), new ItemDto(1L, "Item1", "Description", true, null, null, null, null, List.of()), null);

        when(bookingService.bookingOfState(eq(userId), eq(state))).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].item.name", is("Item1")));

        verify(bookingService, times(1)).bookingOfState(eq(userId), eq(state));
    }
}
