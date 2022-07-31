package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking createBooking(BookingDtoCreate bookingDto, long userId) throws NotFoundException, ValidationException, UnavailableException, AccessException {
        Optional<User> booker = userRepository.findById(userId);
        if (booker.isEmpty()) {
            throw new NotFoundException("Пользователь id " + userId + " не найден");
        }
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            throw new NotFoundException("Предмет id " + bookingDto.getItemId() + " не найден");
        }
        if (item.get().getOwner() == userId) {
            throw new AccessException("Вы не можете забронировать свой предмет");
        }
        if (isUnavailable(item.get())) {
            throw new UnavailableException("Предмет недоступен для бронирования");
        }

        if (
            bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
            bookingDto.getEnd().isBefore(ChronoLocalDateTime.from(LocalDateTime.now())) ||
            bookingDto.getStart().isBefore(ChronoLocalDateTime.from(LocalDateTime.now()))
        ) {
            throw new ValidationException("Ошибка валидации");
        }
        Booking booking = new Booking(
                0L,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item.get(),
                booker.get(),
                WAITING
        );
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(long bookingId, boolean approved, long userId) throws NotFoundException, NotOwnerException, AlreadyApprovedException {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Запроса на бронирование id " + bookingId + " не найден");
        }
        if (booking.get().getItem().getOwner() != userId) {
            throw new NotOwnerException(
                    "Пользователь id " + userId + " не является владельцем предмета " + booking.get().getItem()
            );
        }
        if (booking.get().getStatus().equals(APPROVED)) {
            throw new AlreadyApprovedException("Запрос уже подтверждён");
        }
        return patch(booking.get(), approved);
    }

    @Override
    public Booking findBooking(long bookingId, long userId) throws NotFoundException, AccessException {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Запроса на бронирование id " + bookingId + " не найден");
        }
        if (!hasViewAccess(booking.get(), userId)) {
            throw new AccessException("Ошибка доступа");
        }
        return booking.get();
    }

    @Override
    public List<Booking> findUserBookingsByState(long userId, BookingState state) {
        switch (state){
            case CURRENT:
                return bookingRepository.findUserCurrent(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.findUserPast(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findUserFuture(userId, LocalDateTime.now());
            case WAITING:
            case REJECTED:
                return bookingRepository.findUserByStatus(userId, state);
            default:
                return bookingRepository.findByUserId(userId);
        }
    }

    @Override
    public List<Booking> findOwnerBookingsByState(long userId, BookingState state) {
        switch (state) {
            case CURRENT:
                return bookingRepository.findCurrentByOwner(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.findPastByOwner(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findFutureByOwner(userId, LocalDateTime.now());
            case WAITING:
            case REJECTED:
                return bookingRepository.findByOwnerAndStatus(userId, state);
            default:
                return bookingRepository.findByOwner(userId);
        }
    }

    private Booking patch(Booking booking, boolean approved) {
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        return bookingRepository.save(booking);
    }

    private boolean hasViewAccess(Booking booking, long id) {
        return List.of(booking.getItem().getOwner(), booking.getBooker().getId())
                .contains(id);
    }

    private boolean isUnavailable(Item item) {
        return !item.getAvailable();
    }
}
