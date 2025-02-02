package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailsConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto create(User user) {
        if (userRepository.findUserByEmail((user.getEmail())).isPresent()) {
            throw new EmailsConflictException("Email already exist");
        }

        return UserMapper.toDto(userRepository.save(user));
    }

    public UserDto update(User newUser) {
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

        return UserMapper.toDto(userRepository.save(oldUser));
    }

    public void delete(long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "User with id=%d not found".formatted(id))
                );

        userRepository.deleteById(id);
    }

    public UserDto findById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "User with id=%d not found".formatted(id))
                );

        return UserMapper.toDto(user);
    }
}
