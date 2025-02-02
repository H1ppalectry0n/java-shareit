package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemCreateDto dto) {
        log.info("create item {}, userId {}", dto, userId);
        return itemClient.postItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody @Valid ItemCreateDto dto) {
        log.info("update item {}, userId {}, item id {}", dto, userId, itemId);
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("find item by id {}, userId {}", itemId, userId);
        return itemClient.findByItemId(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("find items by userId {}", userId);
        return itemClient.findByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByName(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam("text") String namePart) {
        log.info("search items by name {}, userId {}", namePart, userId);
        return itemClient.searchByPostName(userId, namePart);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid CommentDto dto, @PathVariable long itemId) {
        log.info("post comment {}, userId {}, itemId {}", dto, userId, itemId);
        return itemClient.postComment(userId, itemId, dto);
    }

}
