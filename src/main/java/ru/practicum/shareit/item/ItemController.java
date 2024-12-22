package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;

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
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto dto) {
        log.trace("create: %d %s".formatted(userId, dto.toString()));
        return ItemMapper.toItemDto(itemService.create(userId, dto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto dto) {
        log.trace("update: %d %d %s".formatted(userId, itemId, dto.toString()));
        return ItemMapper.toItemDto(itemService.update(userId, itemId, dto));
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.trace("findById: %d %d".formatted(userId, itemId));
        return ItemMapper.toItemDto(itemService.findById(itemId));
    }

    @GetMapping
    public List<ItemDto> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.trace("findByUserId: %d".formatted(userId));
        return itemService.findByUserId(userId).stream().map(ItemMapper::toItemDto).toList();
    }

    @GetMapping("/search")
    public List<ItemDto> searchByName(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam("text") String namePart) {
        log.trace("searchByName: %d %s".formatted(userId, namePart));
        return itemService.searchByName(namePart).stream().map(ItemMapper::toItemDto).toList();
    }
}
