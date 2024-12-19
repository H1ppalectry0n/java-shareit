package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    public Item create(Item item);

    public Item update(Item item);

    public Optional<Item> findById(long id);

    public List<Item> findByUserId(long id);

    public List<Item> searchByName(String namePart);
}
