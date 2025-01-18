package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;

import java.awt.print.Book;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    public final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid BookingCreateDto dto) {
        log.trace("create: %d %s".formatted(userId, dto.toString()));
        return BookingMapper.toDto(bookingService.create(userId, dto));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approved(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId, @RequestParam boolean approved) {
        log.trace("approved: %d %b".formatted(userId, approved));
        return BookingMapper.toDto(bookingService.approved(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto bookingStatus(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.trace("bookingStatus: %d %d".formatted(userId, bookingId));
        return BookingMapper.toDto(bookingService.bookingStatus(userId, bookingId));
    }

    @GetMapping
    public List<BookingDto> bookingOfState(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "ALL") BookingState state) {
        log.trace("bookingOfState: %d %s".formatted(userId, state));
        return bookingService.bookingOfState(userId, state).stream().map(BookingMapper::toDto).toList();

    }
}
