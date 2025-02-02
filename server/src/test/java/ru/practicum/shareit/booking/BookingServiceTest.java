package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@Profile("test")
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();

        user = userRepository.save(new User(null, "Test User", "user@test.com"));
        owner = userRepository.save(new User(null, "Owner User", "owner@test.com"));
        item = itemRepository.save(new Item(null, "Test Item", "Description", true, owner, null));
    }

    @Test
    void createBookingShouldCreateNewBooking() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.create(user.getId(), dto);

        assertNotNull(createdBooking);
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
        assertEquals(user.getId(), createdBooking.getBooker().getId());
        assertEquals(item.getId(), createdBooking.getItem().getId());
    }

    @Test
    void createBookingItemNotAvailable() {
        item.setIsAvailable(false);
        itemRepository.save(item);

        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(user.getId(), dto))
                .isInstanceOf(PermissionException.class)
                .hasMessage("Item is not available");
    }

    @Test
    void createBookingUserNotFound() {
        item.setIsAvailable(false);
        itemRepository.save(item);

        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(user.getId() + 65, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id=%d not found".formatted(user.getId() + 65));
    }

    @Test
    void createBookingItemNotFound() {
        item.setIsAvailable(false);
        itemRepository.save(item);

        BookingCreateDto dto = new BookingCreateDto(item.getId() + 5, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(user.getId(), dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item with id=%d not found".formatted(item.getId() + 5));
    }


    @Test
    void approveBookingShouldApproveBooking() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(user.getId(), dto);

        BookingDto approvedBooking = bookingService.approved(owner.getId(), createdBooking.getId(), true);

        assertNotNull(approvedBooking);
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void approveBookingNotOwner() {
        User anotherUser = userRepository.save(new User(null, "Another User", "another@example.com"));
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto booking = bookingService.create(anotherUser.getId(), dto);

        assertThatThrownBy(() -> bookingService.approved(user.getId(), booking.getId(), true))
                .isInstanceOf(PermissionException.class)
                .hasMessage("Only owner of item may approve booking");
    }

    @Test
    void approveBookingNotBooking() {
        User anotherUser = userRepository.save(new User(null, "Another User", "another@example.com"));
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto booking = bookingService.create(anotherUser.getId(), dto);

        assertThatThrownBy(() -> bookingService.approved(user.getId(), booking.getId() + 65, true))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Booking with id=%d not found".formatted(booking.getId() + 65));
    }

    @Test
    void approveBookingUserNotFound() {
        User anotherUser = userRepository.save(new User(null, "Another User", "another@example.com"));
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto booking = bookingService.create(anotherUser.getId(), dto);

        assertThatThrownBy(() -> bookingService.approved(user.getId() + 65, booking.getId() + 65, true))
                .isInstanceOf(PermissionException.class)
                .hasMessage("User with id=%d not found".formatted(user.getId() + 65));
    }


    @Test
    void bookingStatusShouldReturnBookingDetails() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(user.getId(), dto);

        BookingDto fetchedBooking = bookingService.bookingStatus(user.getId(), createdBooking.getId());

        assertNotNull(fetchedBooking);
        assertEquals(createdBooking.getId(), fetchedBooking.getId());
    }

    @Test
    void bookingStatusNotOwnerOrBooker() {
        User anotherUser = userRepository.save(new User(null, "Another User", "another@example.com"));
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto booking = bookingService.create(anotherUser.getId(), dto);

        assertThatThrownBy(() -> bookingService.bookingStatus(user.getId(), booking.getId()))
                .isInstanceOf(PermissionException.class)
                .hasMessage("Only owner of item or booker may see this");
    }


    @Test
    void bookingOfStateShouldReturnCorrectBookings() {
        BookingCreateDto dto1 = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingCreateDto dto2 = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4));

        bookingService.create(user.getId(), dto1);
        bookingService.create(user.getId(), dto2);

        List<BookingDto> bookings = bookingService.bookingOfState(user.getId(), BookingState.ALL);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    void bookingOfStateCurrent() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        bookingService.create(user.getId(), dto);

        List<BookingDto> currentBookings = bookingService.bookingOfState(user.getId(), BookingState.CURRENT);

        assertThat(currentBookings).isNotEmpty();
    }

    @Test
    void bookingOfStatePast() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2));
        bookingService.create(user.getId(), dto);

        List<BookingDto> pastBookings = bookingService.bookingOfState(user.getId(), BookingState.PAST);

        assertThat(pastBookings).isNotEmpty();
    }

    @Test
    void bookingOfStateFuture() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingService.create(user.getId(), dto);

        List<BookingDto> futureBookings = bookingService.bookingOfState(user.getId(), BookingState.FUTURE);

        assertThat(futureBookings).isNotEmpty();
    }

    @Test
    void bookingOfStateWaiting() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingService.create(user.getId(), dto);

        List<BookingDto> waitingBookings = bookingService.bookingOfState(user.getId(), BookingState.WAITING);

        assertThat(waitingBookings).isNotEmpty();
    }

    @Test
    void bookingOfStateRejected() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto booking = bookingService.create(user.getId(), dto);

        bookingService.approved(item.getOwner().getId(), booking.getId(), false);

        List<BookingDto> rejectedBookings = bookingService.bookingOfState(user.getId(), BookingState.REJECTED);

        assertThat(rejectedBookings).isNotEmpty();
    }

    @Test
    void bookingOfStateUserNotFound() {
        assertThatThrownBy(() -> bookingService.bookingOfState(user.getId() + 65, BookingState.REJECTED))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id=%d not found".formatted(user.getId() + 65));
    }

}