package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {ItemController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    private final MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Test
    void createItemValidData() throws Exception {
        // Создаём валидный DTO
        ItemCreateDto validDto = new ItemCreateDto(null, "Item name", "Item description", true, null);

        // Мокаем вызов client
        Mockito.when(itemClient.postItem(Mockito.anyLong(), Mockito.any(ItemCreateDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        // Выполняем запрос
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content("{\"name\": \"Item name\", \"description\": \"Item description\", \"available\": true}")
                )
                .andExpect(status().isCreated());
    }

    @Test
    void createItemInvalidData() throws Exception {
        // Создаём DTO с пустыми полями
        ItemCreateDto invalidDto = new ItemCreateDto(null, "", "", null, null);

        // Выполняем запрос
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content("{\"name\": \"\", \"description\": \"\", \"available\": null}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemValidData() throws Exception {
        // Создаём валидный DTO
        ItemCreateDto validDto = new ItemCreateDto(null, "Updated name", "Updated description", true, null);

        // Мокаем вызов client
        Mockito.when(itemClient.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        // Выполняем запрос
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content("{\"name\": \"Updated name\", \"description\": \"Updated description\", \"available\": true}")
                )
                .andExpect(status().isOk());
    }

    @Test
    void updateItemInvalidData() throws Exception {
        // Создаём DTO с пустыми полями
        ItemCreateDto invalidDto = new ItemCreateDto(null, "", "", null, null);

        // Выполняем запрос
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content("{\"name\": \"\", \"description\": \"\", \"available\": null}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void postCommentValidData() throws Exception {
        // Мокаем успешный ответ от client
        Mockito.when(itemClient.postComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        // Создаём валидный комментарий
        CommentDto validComment = new CommentDto("This is a valid comment");

        // Выполняем запрос
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content("{\"text\": \"This is a valid comment\"}")
                )
                .andExpect(status().isCreated());
    }

    @Test
    void postCommentInvalidData() throws Exception {
        // Создаём комментарий без текста
        CommentDto invalidComment = new CommentDto(null);

        // Выполняем запрос
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content("{\"text\": null}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByIdValidItem() throws Exception {
        // Мокаем успешный ответ от client
        Mockito.when(itemClient.findByItemId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        // Выполняем запрос
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk());
    }

    @Test
    void findByIdItemNotFound() throws Exception {
        // Мокаем ответ "Not Found" от client
        Mockito.when(itemClient.findByItemId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        // Выполняем запрос
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void searchByNameValidQuery() throws Exception {
        // Мокаем успешный ответ от client
        Mockito.when(itemClient.searchByPostName(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().build());

        // Выполняем запрос
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "Item")
                )
                .andExpect(status().isOk());
    }

    @Test
    void searchByNameInvalidQuery() throws Exception {
        // Выполняем запрос без параметра 'text'
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().is5xxServerError());
    }

}