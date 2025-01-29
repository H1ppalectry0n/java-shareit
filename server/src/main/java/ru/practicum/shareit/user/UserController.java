package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.validation.CreateUserValidation;
import ru.practicum.shareit.user.validation.UpdateUserValidation;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    public final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Validated(CreateUserValidation.class) UserDto user) {
        log.info("create: %s".formatted(user.toString()));
        return userService.create(UserMapper.toUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody @Validated(UpdateUserValidation.class) UserDto user, @PathVariable Long userId) {
        log.info("update: %d %s".formatted(userId, user.toString()));
        user.setId(userId);
        return userService.update(UserMapper.toUser(user));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("delete: %d".formatted(userId));
        userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.info("findById: %d".formatted(userId));
        return userService.findById(userId);
    }
}
