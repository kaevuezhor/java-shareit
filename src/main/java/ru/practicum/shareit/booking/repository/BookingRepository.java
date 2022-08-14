package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b " +
            "where b.item.owner = :userId ")
    List<Booking> findByOwner(long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = :userId " +
            "and :currentTime between b.start and b.end ")
    List<Booking> findCurrentByOwner(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = :userId " +
            "and :currentTime > b.end ")
    List<Booking> findPastByOwner(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = :userId " +
            "and :currentTime < b.start ")
    List<Booking> findFutureByOwner(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = :userId " +
            "and b.status = :status ")
    List<Booking> findByOwnerAndStatus(long userId, BookingStatus status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and not b.status = 'REJECTED' ")
    List<Booking> findByUserId(long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and :currentTime between b.start and b.end ")
    List<Booking> findUserCurrent(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and :currentTime between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findUserCurrent(long userId, LocalDateTime currentTime);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and :currentTime > b.end ")
    List<Booking> findUserPast(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and :currentTime > b.end " +
            "order by b.start desc ")
    List<Booking> findUserPast(long userId, LocalDateTime currentTime);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and :currentTime < b.start ")
    List<Booking> findUserFuture(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and b.status = :status ")
    List<Booking> findUserByStatus(long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemIdOrderByStart(long itemId);
}
