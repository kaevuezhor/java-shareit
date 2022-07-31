package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * // TODO .
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(
            @RequestBody BookingDtoCreate booking,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) throws NotFoundException, ValidationException, UnavailableException, AccessException {
        Booking savedBooking = bookingService.createBooking(booking, userId);
        log.info("Создан запрос аренды {}", booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId
            ) throws NotFoundException, NotOwnerException, AlreadyApprovedException {
        Booking patchedBooking = bookingService.approveBooking(bookingId, approved, userId);
        log.info("Изменен статус запроса на бронирование {} на {}", bookingId, approved);
        return BookingMapper.toBookingDto(patchedBooking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBooking(
            @PathVariable long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) throws NotFoundException, AccessException {
        Booking foundBooking = bookingService.findBooking(bookingId, userId);
        log.info("Просмотрен запрос на бронирование {} пользователем {}", bookingId, userId);
        return BookingMapper.toBookingDto(foundBooking);
    }

    @GetMapping
    public List<BookingDto> findUserBookingsByState(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        List<Booking> foundBookings = bookingService.findUserBookingsByState(userId, state);
        log.info("Запрошены бронирования пользователя {} в статусе {}",  userId, state);
        return foundBookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingsByUserAndState(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        List<Booking> foundBookings = bookingService.findOwnerBookingsByState(userId, state);
        log.info("Запрошены бронирования вещей пользователя {} в статусе {}",  userId, state);
        return foundBookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
