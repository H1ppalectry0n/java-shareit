package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.CreateUserValidation;
import ru.practicum.shareit.user.validation.UpdateUserValidation;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    public final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Validated(CreateUserValidation.class) @Valid UserDto user) {
        log.trace("create: %s".formatted(user.toString()));
        return UserMapper.toDto(userService.create(UserMapper.toUser(user)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody @Validated(UpdateUserValidation.class) @Valid UserDto user, @PathVariable Long userId) {
        log.trace("update: %d %s".formatted(userId, user.toString()));
        user.setId(userId);
        return UserMapper.toDto(userService.update(UserMapper.toUser(user)));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.trace("delete: %d".formatted(userId));
        userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.trace("findById: %d".formatted(userId));
        return UserMapper.toDto(userService.findById(userId));
    }
}
