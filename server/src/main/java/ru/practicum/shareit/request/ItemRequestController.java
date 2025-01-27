package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.trace("create: %d %s".formatted(userId, itemRequestDto.toString()));
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> findByRequestorId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.trace("findByRequestorId: %d".formatted(userId));
        return itemRequestService.findAllByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll() {
        log.trace("findAll");
        return itemRequestService.findAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable(name = "requestId", required = true) long id) {
        log.trace("findById: %d".formatted(id));
        return itemRequestService.findById(id);
    }
}
