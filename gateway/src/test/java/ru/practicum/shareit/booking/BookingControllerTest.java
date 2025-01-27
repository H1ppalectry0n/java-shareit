package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    private final MockMvc mvc;

    @MockBean
    private final BookingClient bookingClient;

    @Autowired
    ObjectMapper mapper;

    @Test
    void testFindBookingOfState() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatusCode.valueOf(200));
        when(bookingClient.bookingOfState(anyLong(), any()))
                .thenReturn(response);


        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).bookingOfState(anyLong(), any());
    }

    @Test
    void bookingOfStateInvalidState() throws Exception {
        long userId = 1L;
        String stateParam = "invalid";

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateParam))
                .andExpect(status().is5xxServerError());

        verifyNoInteractions(bookingClient);
    }


    @Test
    void create() throws Exception {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)
        );

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatusCode.valueOf(201));
        when(bookingClient.create(anyLong(), any()))
                .thenReturn(response);


        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        verify(bookingClient, times(1)).create(eq(1L), any(BookItemRequestDto.class));
    }

    @Test
    void approved() throws Exception {
        long userId = 1L;
        long bookingId = 2L;
        boolean approved = true;

        when(bookingClient.approved(userId, bookingId, approved))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).approved(userId, bookingId, approved);
    }

    @Test
    void getBooking() throws Exception {
        long userId = 1L;
        long bookingId = 2L;

        when(bookingClient.getBooking(userId, bookingId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(userId, bookingId);

    }
}