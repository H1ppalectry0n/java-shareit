package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    public List<Item> findByOwnerId(long ownerId);

    public List<Item> findByNameContainingIgnoreCaseAndIsAvailableIsTrue(String namePart);
}
