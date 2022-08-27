package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking createBooking(BookingDtoCreate bookingDto, long userId) throws NotFoundException, UnavailableException, AccessException {
        Optional<User> booker = userRepository.findById(userId);
        if (booker.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            throw new NotFoundException("Предмет " + bookingDto.getItemId() + " не найден");
        }
        if (item.get().getOwner() == userId) {
            throw new AccessException("Вы не можете забронировать свой предмет");
        }
        if (isUnavailable(item.get())) {
            throw new UnavailableException("Предмет недоступен для бронирования");
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
            throw new NotFoundException("Запрос на бронирование " + bookingId + " не найден");
        }
        if (booking.get().getItem().getOwner() != userId) {
            throw new NotOwnerException(
                    "Пользователь " + userId + " не является владельцем предмета " + booking.get().getItem().getId()
            );
        }
        if (booking.get().getStatus().equals(APPROVED)) {
            throw new AlreadyApprovedException("Запрос уже подтверждён");
        }
        return patch(booking.get(), approved);
    }

    @Override
    public Booking findBooking(long bookingId, long userId) throws Throwable {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        return booking.orElseThrow((Supplier<Throwable>) () -> new NotFoundException("Запрос на бронирование " + bookingId + " не найден"));
    }

    @Override
    public List<Booking> findUserBookingsByState(long userId, BookingState state, int from, int size) throws NotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("start")));
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllCurrentByBookerId(userId, LocalDateTime.now(), pageRequest);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), pageRequest);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), pageRequest);
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.valueOf(String.valueOf(state)), pageRequest);
            case ALL:
            default:
                return bookingRepository.findAllByBookerId(userId, pageRequest);
        }
    }

    @Override
    public List<Booking> findOwnerBookingsByState(long userId, BookingState state, int from, int size) throws NotFoundException {
        Optional<User> owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("start")));
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllCurrentByItemOwner(userId, LocalDateTime.now(), pageRequest);
            case PAST:
                return bookingRepository.findAllByItemOwnerAndEndBefore(userId, LocalDateTime.now(), pageRequest);
            case FUTURE:
                return bookingRepository.findAllByItemOwnerAndStartAfter(userId, LocalDateTime.now(), pageRequest);
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllByItemOwnerAndStatus(userId, BookingStatus.valueOf(String.valueOf(state)), pageRequest);
            case ALL:
            default:
                return bookingRepository.findAllByItemOwner(userId, pageRequest);
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

    private boolean isUnavailable(Item item) {
        return !item.getAvailable();
    }
}
