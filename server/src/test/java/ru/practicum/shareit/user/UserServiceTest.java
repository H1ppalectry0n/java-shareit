package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailsConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Profile("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private final UserService userService;

    private final UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        User user = new User(null, "John Doe", "john@example.com");

        User createdUser = userService.create(user);

        // Проверяем, что пользователь был добавлен в базу данных
        List<User> users = userRepository.findAll();
        assertEquals(1, users.size());
        assertEquals(createdUser.getId(), users.get(0).getId());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("john@example.com", users.get(0).getEmail());
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        User user1 = new User(null, "John Doe", "john@example.com");
        userRepository.save(user1); // Сохраняем первого пользователя

        User user2 = new User(null, "Jane Doe", "john@example.com");

        assertThrows(EmailsConflictException.class, () -> userService.create(user2));

        // Убедимся, что в базе данных остался только первый пользователь
        List<User> users = userRepository.findAll();
        assertEquals(1, users.size());
        assertEquals(user1.getId(), users.get(0).getId());
    }

    @Test
    void testUpdateUser() {
        User user = new User(null, "John Doe", "john@example.com");
        User savedUser = userRepository.save(user);

        User updatedUser = new User(savedUser.getId(), "John Smith", "john.smith@example.com");
        User result = userService.update(updatedUser);

        // Проверяем, что изменения сохранены в базе данных
        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertEquals("John Smith", foundUser.getName());
        assertEquals("john.smith@example.com", foundUser.getEmail());
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        User user1 = new User(null, "John Doe", "john@example.com");
        userRepository.save(user1);

        User user2 = new User(null, "Jane Doe", "jane@example.com");
        userRepository.save(user2);

        User updatedUser = new User(user1.getId(), "John Smith", "jane@example.com");

        assertThrows(EmailsConflictException.class, () -> userService.update(updatedUser));

        // Убедимся, что данные в базе не изменились
        User foundUser = userRepository.findById(user1.getId()).orElseThrow();
        assertEquals("John Doe", foundUser.getName());
        assertEquals("john@example.com", foundUser.getEmail());
    }

    @Test
    void testFindUserById() {
        User user = new User(null, "John Doe", "john@example.com");
        User savedUser = userRepository.save(user);

        User foundUser = userService.findById(savedUser.getId());

        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals("John Doe", foundUser.getName());
        assertEquals("john@example.com", foundUser.getEmail());
    }

    @Test
    void testFindUserByIdNotFound() {
        assertThrows(NotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testDeleteUser() {
        User user = new User(null, "John Doe", "john@example.com");
        User savedUser = userRepository.save(user);

        userService.delete(savedUser.getId());

        // Проверяем, что пользователь удален из базы данных
        assertFalse(userRepository.existsById(savedUser.getId()));
        assertEquals(0, userRepository.findAll().size());
    }

    @Test
    void testDeleteUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.delete(1L));

        // Убедимся, что база данных осталась пустой
        assertEquals(0, userRepository.findAll().size());
    }
}
