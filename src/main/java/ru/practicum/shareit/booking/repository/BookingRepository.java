package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemOwner(long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = :userId " +
            "and :currentTime between b.start and b.end ")
    List<Booking> findAllCurrentByItemOwner(long userId, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndBefore(long userId, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartAfter(long userId, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStatus(long userId, BookingStatus status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and not b.status = 'REJECTED' ")
    List<Booking> findAllByBookerId(long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and :currentTime between b.start and b.end ")
    List<Booking> findAllByBookerIdCurrent(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and :currentTime between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdCurrent(long userId, LocalDateTime currentTime);

    List<Booking> findAllByBookerIdAndEndBefore(long userId, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(long userId, LocalDateTime currentTime);

    List<Booking> findAllByBookerIdAndStartAfter(long userId, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemIdOrderByStart(long itemId);
}
