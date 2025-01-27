package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Profile("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest {

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(null, "John Doe", "john.doe@example.com");
        user = userRepository.save(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Need a drill");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
    }

    @Test
    void createItem() {
        ItemCreateDto itemCreateDto = new ItemCreateDto(null, "Drill", "Powerful drill", true, null);

        ItemDto createdItem = itemService.create(user.getId(), itemCreateDto);

        Optional<Item> savedItem = itemRepository.findById(createdItem.getId());
        assertThat(savedItem).isPresent();
        assertThat(savedItem.get().getName()).isEqualTo("Drill");
        assertThat(savedItem.get().getDescription()).isEqualTo("Powerful drill");
        assertThat(savedItem.get().getIsAvailable()).isTrue();
        assertThat(savedItem.get().getOwner().getId()).isEqualTo(user.getId());
    }

    @Test
    void createItemWithRequest() {
        ItemCreateDto itemCreateDto = new ItemCreateDto(null, "Hammer", "Heavy hammer", true, itemRequest.getId());

        ItemDto createdItem = itemService.create(user.getId(), itemCreateDto);

        Optional<Item> savedItem = itemRepository.findById(createdItem.getId());
        assertThat(savedItem).isPresent();
        assertThat(savedItem.get().getName()).isEqualTo("Hammer");
        assertThat(savedItem.get().getRequest().getId()).isEqualTo(itemRequest.getId());
    }

    @Test
    void updateItem() {
        Item item = new Item(null, "Old Name", "Old Description", true, user, null);
        item = itemRepository.save(item);

        ItemCreateDto updatedItemDto = new ItemCreateDto(null, "New Name", "New Description", false, null);

        itemService.update(user.getId(), item.getId(), updatedItemDto);

        Optional<Item> updatedItem = itemRepository.findById(item.getId());
        assertThat(updatedItem).isPresent();
        assertThat(updatedItem.get().getName()).isEqualTo("New Name");
        assertThat(updatedItem.get().getDescription()).isEqualTo("New Description");
        assertThat(updatedItem.get().getIsAvailable()).isFalse();
    }

    @Test
    void searchItemsByName() {
        Item item1 = new Item(null, "Drill", "Powerful drill", true, user, null);
        Item item2 = new Item(null, "Hammer", "Heavy hammer", true, user, null);
        itemRepository.save(item1);
        itemRepository.save(item2);

        var results = itemService.searchByName("drill");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Drill");
    }
}
