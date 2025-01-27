package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailsConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        if (userRepository.findUserByEmail((user.getEmail())).isPresent()) {
            throw new EmailsConflictException("Email already exist");
        }

        return userRepository.save(user);
    }

    public User update(User newUser) {
        User oldUser = userRepository.findById(newUser.getId())
                .orElseThrow(() -> new NotFoundException(
                        "User with id=%d not found".formatted(newUser.getId()))
                );

        if (newUser.getEmail() != null && userRepository.findUserByEmail((newUser.getEmail())).isPresent()) {
            throw new EmailsConflictException("Email already exist");
        }

        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }

        return userRepository.save(oldUser);
    }

    public void delete(long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "User with id=%d not found".formatted(id))
                );

        userRepository.deleteById(id);
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "User with id=%d not found".formatted(id))
                );
    }
}
