package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b " +
            "where b.item.owner = :userId " +
            "order by b.start desc")
    List<Booking> findByOwner(long userId);

    @Query("select b from Booking b " +
            "where b.item.owner = :userId " +
            "and :currentTime between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findCurrentByOwner(long userId, LocalDateTime currentTime);

    @Query("select b from Booking b " +
            "where b.item.owner = :userId " +
            "and :currentTime > b.end " +
            "order by b.start desc")
    List<Booking> findPastByOwner(long userId, LocalDateTime currentTime);

    @Query("select b from Booking b " +
            "where b.item.owner = :userId " +
            "and :currentTime < b.start " +
            "order by b.start desc")
    List<Booking> findFutureByOwner(long userId, LocalDateTime currentTime);

    @Query("select b from Booking b " +
            "where b.item.owner = :userId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> findByOwnerAndStatus(long userId, BookingStatus status);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and not b.status = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> findByUserId(long userId);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and :currentTime between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findUserCurrent(long userId, LocalDateTime currentTime);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and :currentTime > b.end " +
            "order by b.start desc")
    List<Booking> findUserPast(long userId, LocalDateTime currentTime);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and :currentTime < b.start " +
            "order by b.start desc")
    List<Booking> findUserFuture(long userId, LocalDateTime currentTime);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> findUserByStatus(long userId, BookingStatus status);

    List<Booking> findByItemIdOrderByStart(long itemId);
}
