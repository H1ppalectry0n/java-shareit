package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void testCreateItem() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto(null, "Item1", "Description1", true, null);
        ItemDto responseDto = new ItemDto(1L, "Item1", "Description1", true, null, null, null, null, List.of());

        Mockito.when(itemService.create(Mockito.eq(1L), Mockito.any(ItemCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item1\", \"description\":\"Description1\", \"available\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Item1")))
                .andExpect(jsonPath("$.description", is("Description1")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto responseDto = new ItemDto(1L, "UpdatedItem", "UpdatedDescription", false, null, null, null, null, List.of());

        Mockito.when(itemService.update(Mockito.eq(1L), Mockito.eq(1L), Mockito.any(ItemCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedItem\", \"description\":\"UpdatedDescription\", \"available\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("UpdatedItem")))
                .andExpect(jsonPath("$.description", is("UpdatedDescription")))
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    void testFindById() throws Exception {
        ItemDto responseDto = new ItemDto(1L, "Item1", "Description1", true, null, null, null, null, List.of());

        Mockito.when(itemService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Item1")))
                .andExpect(jsonPath("$.description", is("Description1")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void testFindByUserId() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto(1L, "Item1", "Description1", true, null, null, null, null, List.of()),
                new ItemDto(2L, "Item2", "Description2", false, null, null, null, null, List.of())
        );

        Mockito.when(itemService.findByUserId(1L)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void testSearchByName() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto(1L, "SearchItem", "Description", true, null, null, null, null, List.of())
        );

        Mockito.when(itemService.searchByName("Search"))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "Search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("SearchItem")));
    }

    @Test
    void testPostComment() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Great item!", "User1", LocalDateTime.now());
        Comment comment = new Comment();

        Mockito.when(itemService.postComment(Mockito.eq(1L), Mockito.any(CommentDto.class), Mockito.eq(1L)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Great item!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Great item!")))
                .andExpect(jsonPath("$.authorName", is("User1")));
    }
}
