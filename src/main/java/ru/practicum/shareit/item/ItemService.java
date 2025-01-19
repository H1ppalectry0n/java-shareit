package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    public Item create(long userId, Item item) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        item.setOwner(user);

        return itemRepository.save(item);
    }

    public Item update(long userId, long itemId, Item newItem) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        Item oldItem = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id=%d not found".formatted(itemId))
        );

        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            oldItem.setName(newItem.getName());
        }

        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
            oldItem.setDescription(newItem.getDescription());
        }

        if (newItem.getIsAvailable() != null) {
            oldItem.setIsAvailable(newItem.getIsAvailable());
        }

        return itemRepository.save(oldItem);
    }

    public ItemDto findById(long itemId) {
        LocalDateTime now = LocalDateTime.now();

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                "Item with id=%d not found".formatted(itemId)
        ));

        // что обозначает это поле? Последнее завершенного бронирование или текущее бронирование?
        // если последнее завершенное, то не понятно почему в тестах требуется, чтобы оно было null
        // т.к. в тестах отставляют комментарий, но комментарий можно оставить только если бронирование завершено
        // из чего следует, что поле lastBooking, в смысле последнее завершенное бронирование, не может быть null.
        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;

        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDateAsc(itemId);
        for (Booking booking : bookings) {
            if (booking.getStartDate().isBefore(now) && booking.getEndDate().isAfter(now)) {
                lastBooking = booking.getStartDate();
            }

            if (booking.getStartDate().isAfter(now)) {
                nextBooking = booking.getStartDate();
                break;
            }
        }

        ItemDto dto = ItemMapper.toItemDto(item);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(commentRepository.findByItemId(itemId).stream().map(CommentMapper::toDto).toList());

        return dto;
    }

    public List<Item> findByUserId(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        return itemRepository.findByOwnerId(userId);
    }

    public List<Item> searchByName(String namePart) {
        if (namePart.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.findByNameContainingIgnoreCaseAndIsAvailableIsTrue(namePart);
    }

    public Comment postComment(long userId, CommentDto dto, long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                "Item with id=%d not found".formatted(itemId)
        ));

        bookingRepository.findByBookerIdAndItemIdAndEndDateBefore(user.getId(), item.getId(), LocalDateTime.now()).orElseThrow(() -> new PermissionException(
                "Post comment not allowed"
        ));

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }
}
