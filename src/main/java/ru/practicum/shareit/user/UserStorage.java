package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserStorage {
    public Optional<User> findById(long id);

    public User create(User user);

    public User update(User user);

    public void delete(long id);
}
