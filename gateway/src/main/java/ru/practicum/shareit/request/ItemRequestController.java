package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info(" post item request {}, userId {}", itemRequestDto, userId);
        return itemRequestClient.postRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findByRequestorId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("find by requestor id {}", userId);
        return itemRequestClient.findByRequestorId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll() {
        log.info("find all");
        return itemRequestClient.findAll();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable(name = "requestId", required = true) long id) {
        log.info("find item request by id {}", id);
        return itemRequestClient.findById(id);
    }

}
