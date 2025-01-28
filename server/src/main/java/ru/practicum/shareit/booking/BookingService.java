package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    public BookingDto create(long userId, BookingCreateDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(
                () -> new NotFoundException("Item with id=%d not found".formatted(dto.getItemId()))
        );

        if (!item.getIsAvailable()) {
            throw new PermissionException("Item is not available");
        }

        Booking booking = BookingMapper.toBooking(dto, user, item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingDto approved(long userId, long bookingId, boolean approved) {
        User user = userRepository.findById(userId).orElseThrow(() -> new PermissionException(
                "User with id=%d not found".formatted(userId)
        ));

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id=%d not found".formatted(bookingId))
        );

        if (!booking.getItem().getOwner().equals(user)) {
            throw new PermissionException("Only owner of item may approve booking");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingDto bookingStatus(long userId, long bookingId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id=%d not found".formatted(bookingId))
        );

        if (!booking.getItem().getOwner().equals(user) && !booking.getBooker().equals(user)) {
            throw new PermissionException("Only owner of item or booker may see this");
        }

        return BookingMapper.toDto(booking);
    }

    public List<BookingDto> bookingOfState(long userId, BookingState state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d not found".formatted(userId)
        ));

        return switch (state) {
            case ALL -> bookingRepository.findByBookerId(userId).stream().map(BookingMapper::toDto).toList();
            case CURRENT ->
                    bookingRepository.findCurrentBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toDto).toList();
            case PAST ->
                    bookingRepository.findPastBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toDto).toList();
            case FUTURE ->
                    bookingRepository.findFutureBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toDto).toList();
            case WAITING ->
                    bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING).stream().map(BookingMapper::toDto).toList();
            case REJECTED ->
                    bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED).stream().map(BookingMapper::toDto).toList();
        };
    }
}
