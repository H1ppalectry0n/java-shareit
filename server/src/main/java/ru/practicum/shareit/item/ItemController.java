package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    public final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemCreateDto dto) {
        log.info("create: %d %s".formatted(userId, dto.toString()));
        return itemService.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemCreateDto dto) {
        log.info("update: %d %d %s".formatted(userId, itemId, dto.toString()));
        return itemService.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("findById: %d %d".formatted(userId, itemId));
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<ItemDto> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("findByUserId: %d".formatted(userId));
        return itemService.findByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByName(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam("text") String namePart) {
        log.info("searchByName: %d %s".formatted(userId, namePart));
        return itemService.searchByName(namePart);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody CommentDto dto, @PathVariable long itemId) {
        log.info("postComment: %d %s".formatted(userId, dto.toString()));
        return itemService.postComment(userId, dto, itemId);
    }
}
