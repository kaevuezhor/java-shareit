package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.*;

import java.util.List;

public interface BookingService {

    Booking createBooking(BookingDtoCreate booking, long userId) throws NotFoundException, UnavailableException, AccessException;

    Booking approveBooking(long bookingId, boolean approved, long userId) throws NotFoundException, NotOwnerException, AlreadyApprovedException;

    Booking findBooking(long bookingId, long userId) throws Throwable;

    List<Booking> findUserBookingsByState(long userId, BookingState state, int from, int size) throws NotFoundException;

    List<Booking> findOwnerBookingsByState(long userId, BookingState state, int from, int size) throws NotFoundException;
}
