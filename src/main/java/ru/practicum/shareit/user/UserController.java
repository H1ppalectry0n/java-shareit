package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public User create(@RequestBody @Validated(CreateUserValidation.class) @Valid User user) {
        log.trace("create: %s".formatted(user.toString()));
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public User update(@RequestBody @Validated(UpdateUserValidation.class) @Valid User user, @PathVariable Long userId) {
        log.trace("update: %d %s".formatted(userId, user.toString()));
        user.setId(userId);
        return userService.update(user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.trace("delete: %d".formatted(userId));
        userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable Long userId) {
        log.trace("findById: %d".formatted(userId));
        return userService.findById(userId);
    }
}
