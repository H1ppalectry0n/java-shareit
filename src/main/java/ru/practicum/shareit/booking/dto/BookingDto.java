package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;

    private UserDto booker;

    @Future(message = "Start date must be in the future")
    @NotNull(message = "Start date cannot be null")
    private LocalDateTime start;

    @Future(message = "End date must be in the future")
    @NotNull(message = "End date cannot be null")
    private LocalDateTime end;

    @NotNull(message = "Item ID cannot be null")
    private ItemDto item;

    private BookingStatus status;
}
