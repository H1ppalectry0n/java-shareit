package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long idForNewItem = 1;

    @Override
    public Item create(Item item) {
        item.setId(idForNewItem++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findByUserId(long id) {
        return items.values().stream().filter(i -> i.getOwner() == id).toList();
    }

    @Override
    public List<Item> searchByName(String namePart) {
        return items.values().stream().filter(i -> i.getName().toLowerCase().contains(namePart.toLowerCase()) && i.getAvailable()).toList();
    }
}
