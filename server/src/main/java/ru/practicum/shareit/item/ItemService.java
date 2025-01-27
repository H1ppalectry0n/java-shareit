package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemDto create(long userId, ItemCreateDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        Item item = ItemMapper.toItem(itemDto, user, null);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException(
                    "ItemRequest with id = %d not found".formatted(itemDto.getRequestId())
            )));
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public Item update(long userId, long itemId, ItemCreateDto newItem) {
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

        if (newItem.getAvailable() != null) {
            oldItem.setIsAvailable(newItem.getAvailable());
        }

        return itemRepository.save(oldItem);
    }

    public ItemDto findById(long itemId) {
        LocalDateTime now = LocalDateTime.now();

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                "Item with id=%d not found".formatted(itemId)
        ));

        ItemDto dto = ItemMapper.toItemDto(item);
        dto.setComments(commentRepository.findByItemId(itemId).stream().map(CommentMapper::toDto).toList());

        return dto;
    }

    public List<ItemDto> findByUserId(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        List<Item> items = itemRepository.findByOwnerId(userId);

        List<Booking> bookings = bookingRepository.findApprovedForItems(items, Sort.by(Sort.Direction.ASC, "endDate"));

        Map<Long, ItemDto> itemsMap = items.stream().collect(Collectors.toMap(Item::getId, ItemMapper::toItemDto));

        for (Booking booking : bookings) {
            ItemDto item = itemsMap.get(booking.getItem().getId());

            if (booking.getEndDate().isBefore(LocalDateTime.now())) {
                item.setLastBooking(booking.getStartDate());
            }

            if (booking.getStartDate().isAfter(LocalDateTime.now()) &&
                    (item.getNextBooking() == null || item.getNextBooking().isAfter(booking.getStartDate()))) {
                item.setNextBooking(booking.getStartDate());
            }
        }

        return itemsMap.values().stream().toList();
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
