package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private final MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testCreateUser() throws Exception {

        UserDto userDto = new UserDto(1L, "user 1", "test@email.com");

        Mockito.when(userService.create(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"user 1\", \"email\" : \"test@email.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("user 1")))
                .andExpect(jsonPath("$.email", is("test@email.com")));

        verify(userService, times(1)).create(any());
    }

    @Test
    void testUserUpdate() throws Exception {
        long userId = 1L;

        UserDto userDto = new UserDto(1L, "user 1", "test@email.com");

        Mockito.when(userService.update(any()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/" + userId)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"user 1\", \"email\" : \"test@email.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("user 1")))
                .andExpect(jsonPath("$.email", is("test@email.com")));

        verify(userService, times(1)).update(any());
    }

    @Test
    void testDeleteUser() throws Exception {
        long userId = 1L;

        Mockito.doNothing().when(userService).delete(anyLong());

        mockMvc.perform(delete("/users/" + userId)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(anyLong());
    }

    @Test
    void testFindUserById() throws Exception {
        long userId = 1L;

        UserDto userDto = new UserDto(1L, "user 1", "test@email.com");

        Mockito.when(userService.findById(1L))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/" + userId)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"user 1\", \"email\" : \"test@email.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("user 1")))
                .andExpect(jsonPath("$.email", is("test@email.com")));

        verify(userService, times(1)).findById(anyLong());

    }

}