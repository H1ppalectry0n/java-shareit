package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Profile("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest {

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    private final BookingRepository bookingRepository;

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
    void createItemNoUser() {
        ItemCreateDto itemCreateDto = new ItemCreateDto(null, "Drill", "Powerful drill", true, null);

        assertThrows(NotFoundException.class, () -> itemService.create(user.getId() + 1, itemCreateDto));
    }

    @Test
    void createItemInvalidRequest() {
        ItemCreateDto itemCreateDto = new ItemCreateDto(null, "Drill", "Powerful drill", true, 3L);

        assertThrows(NotFoundException.class, () -> itemService.create(user.getId(), itemCreateDto));
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
    void updateItemName() {
        Item item = new Item(null, "Old Name", "Old Description", true, user, null);
        item = itemRepository.save(item);

        ItemCreateDto updatedItemDto = new ItemCreateDto(null, "New Name", null, null, null);

        itemService.update(user.getId(), item.getId(), updatedItemDto);

        Optional<Item> updatedItem = itemRepository.findById(item.getId());
        assertThat(updatedItem).isPresent();
        assertThat(updatedItem.get().getName()).isEqualTo("New Name");
    }

    @Test
    void updateItemDescription() {
        Item item = new Item(null, "Old Name", "Old Description", true, user, null);
        item = itemRepository.save(item);

        ItemCreateDto updatedItemDto = new ItemCreateDto(null, null, "New Description", null, null);

        itemService.update(user.getId(), item.getId(), updatedItemDto);

        Optional<Item> updatedItem = itemRepository.findById(item.getId());
        assertThat(updatedItem).isPresent();
        assertThat(updatedItem.get().getDescription()).isEqualTo("New Description");
    }

    @Test
    void updateItemAvailable() {
        Item item = new Item(null, "Old Name", "Old Description", true, user, null);
        item = itemRepository.save(item);

        ItemCreateDto updatedItemDto = new ItemCreateDto(null, null, null, false, null);

        itemService.update(user.getId(), item.getId(), updatedItemDto);

        Optional<Item> updatedItem = itemRepository.findById(item.getId());
        assertThat(updatedItem).isPresent();
        assertThat(updatedItem.get().getIsAvailable()).isFalse();
    }


    @Test
    void updateItemInvalidUser() {
        Item item1 = new Item(null, "Old Name", "Old Description", true, user, null);
        final Item item2 = itemRepository.save(item1);

        ItemCreateDto updatedItemDto = new ItemCreateDto(null, "New Name", "New Description", false, null);

        assertThrows(NotFoundException.class, () -> itemService.update(user.getId() + 1, item2.getId(), updatedItemDto));
    }

    @Test
    void updateItemInvalidItemId() {
        Item item1 = new Item(null, "Old Name", "Old Description", true, user, null);
        final Item item2 = itemRepository.save(item1);

        ItemCreateDto updatedItemDto = new ItemCreateDto(null, "New Name", "New Description", false, null);

        assertThrows(NotFoundException.class, () -> itemService.update(user.getId(), item2.getId() + 1, updatedItemDto));
    }

    @Test
    void testFindByItemId() {
        Item item = new Item(null, "Name", "Description", false, user, null);
        item = itemRepository.save(item);

        ItemDto updatedItem = itemService.findById(item.getId());
        assertThat(updatedItem.getName()).isEqualTo("Name");
        assertThat(updatedItem.getDescription()).isEqualTo("Description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void testFindByInvalidItemId() {
        Item item1 = new Item(null, "Name", "Description", false, user, null);
        Item item2 = itemRepository.save(item1);

        assertThrows(NotFoundException.class, () -> itemService.findById(item2.getId() + 5));
    }

    @Test
    void testFindByUserId() {
        Item item = new Item(null, "Name", "Description", false, user, null);
        itemRepository.save(item);

        Booking lastBooking = new Booking(null, LocalDateTime.now().minusSeconds(2), LocalDateTime.now().minusSeconds(1), item, user, BookingStatus.APPROVED);
        bookingRepository.save(lastBooking);

        Booking newxtBooking = new Booking(null, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user, BookingStatus.APPROVED);
        bookingRepository.save(newxtBooking);


        List<ItemDto> items = itemService.findByUserId(user.getId());

        assertThat(items).hasSize(1);
    }

    @Test
    void testFindByInvalidUserId() {
        Item item1 = new Item(null, "Name", "Description", false, user, null);
        Item item2 = itemRepository.save(item1);

        assertThrows(NotFoundException.class, () -> itemService.findByUserId(item2.getId() + 5));
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

    @Test
    void searchItemsByEmptyName() {
        Item item1 = new Item(null, "Drill", "Powerful drill", true, user, null);
        Item item2 = new Item(null, "Hammer", "Heavy hammer", true, user, null);
        itemRepository.save(item1);
        itemRepository.save(item2);

        var results = itemService.searchByName("");

        assertThat(results).hasSize(0);
    }


    @Test
    void testPostComment() {
        Item item = new Item(null, "Name", "Description", false, user, null);
        item = itemRepository.save(item);

        Booking booking = new Booking(null, LocalDateTime.now().minusSeconds(2), LocalDateTime.now().minusSeconds(1), item, user, BookingStatus.APPROVED);
        booking = bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto(null, "Text", "Authptor", LocalDateTime.now());

        CommentDto dto = itemService.postComment(user.getId(), commentDto, item.getId());

        assertThat(dto.getText()).isEqualTo(commentDto.getText());
    }

    @Test
    void testPostCommentInvalidUserId() {
        Item item = new Item(null, "Name", "Description", false, user, null);
        itemRepository.save(item);

        CommentDto commentDto = new CommentDto(null, "Text", "Authptor", LocalDateTime.now());


        assertThrows(NotFoundException.class, () -> itemService.postComment(user.getId() + 1, commentDto, item.getId()));
    }

    @Test
    void testPostCommentInvalidItemId() {
        Item item = new Item(null, "Name", "Description", false, user, null);
        itemRepository.save(item);

        CommentDto commentDto = new CommentDto(null, "Text", "Authptor", LocalDateTime.now());


        assertThrows(NotFoundException.class, () -> itemService.postComment(user.getId(), commentDto, 2));
    }


    @Test
    void testPostCommentNoBooking() {
        Item item = itemRepository.save(new Item(null, "Name", "Description", false, user, null));

        CommentDto commentDto = new CommentDto(null, "Text", "Authptor", LocalDateTime.now());

        assertThrows(PermissionException.class, () -> itemService.postComment(user.getId(), commentDto, item.getId()));
    }


}
