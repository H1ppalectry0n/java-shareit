package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    public final InMemoryItemStorage itemStorage;
    public final InMemoryUserStorage userStorage;

    public Item create(long userId, ItemDto dto) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        Item item = new Item(null, dto.getName(), dto.getDescription(), dto.getAvailable(), userId);

        return itemStorage.create(item);
    }

    public Item update(long userId, long itemId, ItemDto dto) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id=%d not found".formatted(itemId))
        );

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return itemStorage.update(item);
    }

    public Item findById(long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException(
                "Item with id=%d not found".formatted(itemId)
        ));
    }

    public List<Item> findByUserId(long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        return itemStorage.findByUserId(userId);
    }

    public List<Item> searchByName(String namePart) {
        if (namePart.isBlank()) {
            return Collections.emptyList();
        }

        return itemStorage.searchByName(namePart);
    }
}
