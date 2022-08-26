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

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingDto createBooking(
            @RequestBody BookingDtoCreate booking,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) throws NotFoundException, ValidationException, UnavailableException, AccessException {
        Booking savedBooking = bookingService.createBooking(booking, userId);
        log.info("Создан запрос аренды {}", booking);
        return bookingMapper.toBookingDto(savedBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId
            ) throws NotFoundException, NotOwnerException, AlreadyApprovedException {
        Booking patchedBooking = bookingService.approveBooking(bookingId, approved, userId);
        log.info("Изменен статус запроса на бронирование {} на {}", bookingId, approved);
        return bookingMapper.toBookingDto(patchedBooking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBooking(
            @PathVariable long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) throws Throwable {
        Booking foundBooking = bookingService.findBooking(bookingId, userId);
        if (!hasViewAccess(foundBooking, userId)) {
            throw new AccessException("Ошибка доступа");
        }
        log.info("Просмотрен запрос на бронирование {} пользователем {}", bookingId, userId);
        return bookingMapper.toBookingDto(foundBooking);
    }

    @GetMapping
    public List<BookingDto> findUserBookingsByState(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) throws NotFoundException, ValidationException {
        List<Booking> foundBookings = bookingService.findUserBookingsByState(userId, getState(state), from, size);
        log.info("Запрошены бронирования пользователя {} в статусе {}",  userId, state);
        return foundBookings
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> findOwnerBookingsByState(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) throws NotFoundException, ValidationException {
        List<Booking> foundBookings = bookingService.findOwnerBookingsByState(userId, getState(state), from, size);
        log.info("Запрошены бронирования вещей пользователя {} в статусе {}",  userId, state);
        return foundBookings
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private boolean hasViewAccess(Booking booking, long id) {
        return List.of(booking.getItem().getOwner(), booking.getBooker().getId())
                .contains(id);
    }

    private BookingState getState(String state) throws ValidationException {
        BookingState stateCase;
        try {
            stateCase = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            log.error("Unknown state: {}", state);
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
        return stateCase;
    }
}
