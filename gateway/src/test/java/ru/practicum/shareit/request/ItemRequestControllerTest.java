package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private ItemRequestDto validRequest;
    private ItemRequestDto invalidRequest;

    @BeforeEach
    void setUp() {
        validRequest = new ItemRequestDto("Valid description");
        invalidRequest = new ItemRequestDto("");
    }

    @Test
    void createWhenValidRequestThenReturnsOk() throws Exception {
        Mockito.when(itemRequestClient.postRequest(eq(1L), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        Mockito.verify(itemRequestClient, Mockito.times(1)).postRequest(eq(1L), any(ItemRequestDto.class));
    }

    @Test
    void createWhenInvalidRequestThenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemRequestClient, Mockito.times(0)).postRequest(any(Long.class), any(ItemRequestDto.class));
    }

    @Test
    void findByRequestorIdWhenValidUserIdThenReturnsOk() throws Exception {
        Mockito.when(itemRequestClient.findByRequestorId(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        Mockito.verify(itemRequestClient, Mockito.times(1)).findByRequestorId(1L);
    }

    @Test
    void findByRequestorIdWhenMissingHeaderThenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().is5xxServerError());

        Mockito.verify(itemRequestClient, Mockito.times(0)).findByRequestorId(any(Long.class));
    }

    @Test
    void findAllThenReturnsOk() throws Exception {
        Mockito.when(itemRequestClient.findAll())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk());

        Mockito.verify(itemRequestClient, Mockito.times(1)).findAll();
    }

    @Test
    void findByIdWhenValidIdThenReturnsOk() throws Exception {
        Mockito.when(itemRequestClient.findById(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk());

        Mockito.verify(itemRequestClient, Mockito.times(1)).findById(1L);
    }

    @Test
    void findByIdWhenInvalidIdThenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/requests/invalid"))
                .andExpect(status().is5xxServerError());

        Mockito.verify(itemRequestClient, Mockito.times(0)).findById(any(Long.class));
    }
}
