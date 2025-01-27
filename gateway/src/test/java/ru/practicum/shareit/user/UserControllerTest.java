package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void createValidUser() throws Exception {
        UserDto userDto = new UserDto(null, "Valid Name", "valid@email.com");
        Mockito.when(userClient.create(any(UserDto.class))).thenReturn(ResponseEntity.ok(Map.of("id", 1)));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        Mockito.verify(userClient, Mockito.times(1)).create(any(UserDto.class));
    }

    @Test
    void createInvalidUser() throws Exception {
        UserDto userDto = new UserDto(null, "", "invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userClient, Mockito.never()).create(any(UserDto.class));
    }

    @Test
    void updateValidUser() throws Exception {
        UserDto userDto = new UserDto(null, "Updated Name", "updated@email.com");
        Mockito.when(userClient.update(eq(1L), any(UserDto.class))).thenReturn(ResponseEntity.ok(Map.of("id", 1)));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        Mockito.verify(userClient, Mockito.times(1)).update(eq(1L), any(UserDto.class));
    }

    @Test
    void updateInvalidUser() throws Exception {
        UserDto userDto = new UserDto(null, "", "invalid-email");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userClient, Mockito.never()).update(eq(1L), any(UserDto.class));
    }

    @Test
    void deleteUser() throws Exception {
        Mockito.when(userClient.delete(1L)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userClient, Mockito.times(1)).delete(1L);
    }

    @Test
    void findUserById() throws Exception {
        Mockito.when(userClient.findById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1, "name", "User Name", "email", "user@email.com")));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("User Name"))
                .andExpect(jsonPath("$.email").value("user@email.com"));

        Mockito.verify(userClient, Mockito.times(1)).findById(1L);
    }
}