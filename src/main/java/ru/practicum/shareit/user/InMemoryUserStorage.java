package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long idForNewUser = 1;

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(idForNewUser++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getEmail() != null) {
            users.get(user.getId()).setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            users.get(user.getId()).setName(user.getName());
        }

        return users.get(user.getId());
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    public boolean checkEmailIsFree(String email) {
        return users.values().stream().map(User::getEmail).noneMatch(e -> e.equals(email));
    }
}
