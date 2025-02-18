package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.startDate <= :now AND b.endDate >= :now")
    List<Booking> findCurrentBookings(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.endDate < :now")
    List<Booking> findPastBookings(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.startDate > :now")
    List<Booking> findFutureBookings(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status);

    Optional<Booking> findByBookerIdAndItemIdAndEndDateBefore(long bookerId, long itemId, LocalDateTime before);

    List<Booking> findByItemIdOrderByStartDateAsc(long itemId);

    @Query("SELECT b from Booking b JOIN FETCH b.item i WHERE b.item in ?1 and b.status = 'APPROVED'")
    List<Booking> findApprovedForItems(List<Item> items, Sort sort);
}
