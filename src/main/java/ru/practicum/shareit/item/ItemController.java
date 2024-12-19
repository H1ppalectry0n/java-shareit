package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    public final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto dto) {
        return itemService.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto dto) {
        return itemService.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public Item findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<Item> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findByUserId(userId);
    }

    @GetMapping("/search")
    public List<Item> searchByName(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam("text") String namePart) {
        return itemService.searchByName(namePart);
    }
}
