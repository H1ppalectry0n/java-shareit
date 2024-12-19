package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailsConflictException;
import ru.practicum.shareit.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    public final InMemoryUserStorage userStorage;

    public User create(User user) {
        if (!userStorage.checkEmailIsFree(user.getEmail())) {
            throw new EmailsConflictException("Email already exist");
        }

        return userStorage.create(user);
    }

    public User update(User user) {
        userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(
                        "User with id=%d not found".formatted(user.getId()))
                );

        if (user.getEmail() != null && !userStorage.checkEmailIsFree(user.getEmail())) {
            throw new EmailsConflictException("Email already exist");
        }

        return userStorage.update(user);
    }

    public void delete(long id) {
        userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "User with id=%d not found".formatted(id))
                );

        userStorage.delete(id);
    }

    public User findById(long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "User with id=%d not found".formatted(id))
                );
    }
}
