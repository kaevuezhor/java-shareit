package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    BookingService bookingService;

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    User testUser1;

    User testUser2;

    Item testItem1;

    Item testItem2;

    LocalDateTime testStartDate;

    LocalDateTime testEndDate;

    Booking testBookingWaiting;

    Booking testBookingApproved;




    @BeforeEach
    void setup() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository
        );
        testStartDate = LocalDateTime.of(2022,9,1,1,1);
        testEndDate = LocalDateTime.of(2022,9,2,1,1);
        testUser1 = new User(
                1L,
                "name",
                "e@ma.il"
        );
        testUser2 = new User(
                2L,
                "2name",
                "e2@ma.il"
        );
        testItem1 = new Item(
                1L,
                "item",
                "eto item",
                true,
                1L,
                null
        );
        testItem2 = new Item(
                2L,
                "item unavailable",
                "etot item ne dostupen",
                false,
                1L,
                null
        );
        testBookingWaiting = new Booking(
                1L,
                testStartDate,
                testEndDate,
                testItem1,
                testUser2,
                WAITING
        );
        testBookingApproved = new Booking(
                1L,
                testStartDate,
                testEndDate,
                testItem1,
                testUser2,
                APPROVED
        );
    }

    @Test
    void testCreateBooking() throws ValidationException, AccessException, UnavailableException, NotFoundException {
        long userId = 2L;
        long itemOwnerId = 1L;
        long wrongUserId = 6L;
        long wrongItemId = 6L;
        BookingDtoCreate requestBody = new BookingDtoCreate(
                1L,
                testStartDate,
                testEndDate
        );
        BookingDtoCreate requestBodyWrongItemId = new BookingDtoCreate(
                6L,
                testStartDate,
                testEndDate
        );
        BookingDtoCreate requestBodyUnavailableItemId = new BookingDtoCreate(
                2L,
                testStartDate,
                testEndDate
        );
        BookingDtoCreate requestBodyWrongStart = new BookingDtoCreate(
                1L,
                testEndDate,
                testStartDate
        );
        Booking savingBooking = new Booking(
                0L,
                requestBody.getStart(),
                requestBody.getEnd(),
                testItem1,
                testUser2,
                WAITING
        );
        Booking expectedBooking = new Booking(
                1L,
                requestBody.getStart(),
                requestBody.getEnd(),
                testItem1,
                testUser2,
                WAITING
        );

        Mockito
                .when(userRepository.findById(testUser1.getId()))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(userRepository.findById(testUser2.getId()))
                .thenReturn(Optional.of(testUser2));

        Mockito
                .when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        Mockito
                .when(itemRepository.findById(testItem1.getId()))
                .thenReturn(Optional.of(testItem1));

        Mockito
                .when(itemRepository.findById(testItem2.getId()))
                .thenReturn(Optional.of(testItem2));

        Mockito
                .when(itemRepository.findById(wrongItemId))
                .thenReturn(Optional.empty());

        Mockito
                .when(bookingRepository.save(savingBooking))
                .thenReturn(testBookingWaiting);

        final NotFoundException userNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(requestBody, wrongUserId)
        );

        Assertions.assertEquals("Пользователь 6 не найден", userNotFoundException.getMessage());

        final NotFoundException itemNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(requestBodyWrongItemId, userId)
        );

        Assertions.assertEquals("Предмет 6 не найден", itemNotFoundException.getMessage());

        final AccessException ownerAccessException = Assertions.assertThrows(
                AccessException.class,
                () -> bookingService.createBooking(requestBody, itemOwnerId)
        );

        Assertions.assertEquals("Вы не можете забронировать свой предмет", ownerAccessException.getMessage());

        final UnavailableException itemUnavailableException = Assertions.assertThrows(
                UnavailableException.class,
                () -> bookingService.createBooking(requestBodyUnavailableItemId, userId)
        );

        Assertions.assertEquals("Предмет недоступен для бронирования", itemUnavailableException.getMessage());

        final ValidationException validationException = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(requestBodyWrongStart, userId)
        );

        Assertions.assertEquals("Ошибка валидации", validationException.getMessage());

        Booking savedBooking = bookingService.createBooking(requestBody, userId);

        Assertions.assertEquals(expectedBooking, savedBooking);
    }

    @Test
    void testApproveBooking() throws NotOwnerException, NotFoundException, AlreadyApprovedException {
        long bookingId = 1L;
        long wrongBookingId = 2L;
        boolean approved = true;
        long userId = 1L;
        long bookerId = 2L;

        Mockito
                .when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(testBookingWaiting));

        Mockito
                .when(bookingRepository.findById(wrongBookingId))
                .thenReturn(Optional.empty());

        Mockito
                .when(bookingRepository.save(testBookingApproved))
                .thenReturn(testBookingApproved);

        final NotFoundException bookingNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approveBooking(wrongBookingId, approved, userId)
        );

        Assertions.assertEquals("Запрос на бронирование 2 не найден", bookingNotFoundException.getMessage());

        final NotOwnerException notOwnerException = Assertions.assertThrows(
                NotOwnerException.class,
                () -> bookingService.approveBooking(bookingId, approved, bookerId)
        );

        Assertions.assertEquals("Пользователь 2 не является владельцем предмета 1", notOwnerException.getMessage());

        Booking approvedBooking = bookingService.approveBooking(bookingId, true, userId);

        Assertions.assertEquals(testBookingApproved, approvedBooking);

        Mockito
                .when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(testBookingApproved));

        final AlreadyApprovedException alreadyApprovedException = Assertions.assertThrows(
                AlreadyApprovedException.class,
                () -> bookingService.approveBooking(bookingId, approved, userId)
        );

        Assertions.assertEquals("Запрос уже подтверждён", alreadyApprovedException.getMessage());
    }

    @Test
    void testFindBooking() throws Throwable {
        long bookingId = 1L;
        long wrongBookingId = 6L;
        long userId = 1L;
        long wrongUserId = 6L;

        Mockito
                .when(userRepository.findById(testUser1.getId()))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        Mockito
                .when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(testBookingApproved));

        Mockito
                .when(bookingRepository.findById(wrongBookingId))
                .thenReturn(Optional.empty());

        final NotFoundException userNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(bookingId, wrongUserId)
        );

        Assertions.assertEquals("Пользователь 6 не найден", userNotFoundException.getMessage());

        final NotFoundException bookingNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(wrongBookingId, userId)
        );

        Assertions.assertEquals("Запрос на бронирование 6 не найден", bookingNotFoundException.getMessage());

        Booking foundBooking = bookingService.findBooking(bookingId, userId);

        Assertions.assertEquals(foundBooking, testBookingApproved);
    }

    @Test
    void testFindUserBookingsByState() throws ValidationException, NotFoundException {
        long userId = 1L;
        long wrongUserId = 6L;
        int from = 0;
        int size = 1;

        Mockito
                .when(userRepository.findById(testUser1.getId()))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        final NotFoundException userNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findUserBookingsByState(wrongUserId, BookingState.ALL, from, size)
        );

        Assertions.assertEquals("Пользователь 6 не найден", userNotFoundException.getMessage());

        bookingService.findUserBookingsByState(userId, BookingState.CURRENT, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findUserCurrent(eq(userId), any(LocalDateTime.class), any(PageRequest.class));
                .findAllByBookerIdCurrent(eq(userId), any(LocalDateTime.class), any(PageRequest.class));

        bookingService.findUserBookingsByState(userId, BookingState.PAST, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findUserPast(eq(userId), any(LocalDateTime.class), any(PageRequest.class));
                .findAllByBookerIdAndEndBefore(eq(userId), any(LocalDateTime.class), any(PageRequest.class));

        bookingService.findUserBookingsByState(userId, BookingState.FUTURE, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findUserFuture(eq(userId), any(LocalDateTime.class), any(PageRequest.class));
                .findAllByBookerIdAndStartAfter(eq(userId), any(LocalDateTime.class), any(PageRequest.class));

        bookingService.findUserBookingsByState(userId, BookingState.WAITING, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findUserByStatus(eq(userId), any(BookingStatus.class), any(PageRequest.class));
                .findAllByBookerIdAndStatus(eq(userId), any(BookingStatus.class), any(PageRequest.class));

        bookingService.findUserBookingsByState(userId, BookingState.REJECTED, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findUserByStatus(eq(userId), any(BookingStatus.class), any(PageRequest.class));
                .findAllByBookerIdAndStatus(eq(userId), any(BookingStatus.class), any(PageRequest.class));

        bookingService.findUserBookingsByState(userId, BookingState.ALL, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findByUserId(eq(userId), any(PageRequest.class));
                .findAllByBookerId(eq(userId), any(PageRequest.class));
    }

    @Test
    void testFindOwnerBookingsByState() throws ValidationException, NotFoundException {
        long userId = 1L;
        long wrongUserId = 6L;
        int from = 0;
        int size = 1;

        Mockito
                .when(userRepository.findById(testUser1.getId()))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        final NotFoundException userNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findOwnerBookingsByState(wrongUserId, BookingState.ALL, from, size)
        );

        Assertions.assertEquals("Пользователь 6 не найден", userNotFoundException.getMessage());

        bookingService.findOwnerBookingsByState(userId, BookingState.CURRENT, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findCurrentByOwner(eq(userId), any(LocalDateTime.class), any(PageRequest.class));
                .findAllCurrentByItemOwner(eq(userId), any(LocalDateTime.class), any(PageRequest.class));

        bookingService.findOwnerBookingsByState(userId, BookingState.PAST, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findPastByOwner(eq(userId), any(LocalDateTime.class), any(PageRequest.class));
                .findAllByItemOwnerAndEndBefore(eq(userId), any(LocalDateTime.class), any(PageRequest.class));

        bookingService.findOwnerBookingsByState(userId, BookingState.FUTURE, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findFutureByOwner(eq(userId), any(LocalDateTime.class), any(PageRequest.class));
                .findAllByItemOwnerAndStartAfter(eq(userId), any(LocalDateTime.class), any(PageRequest.class));

        bookingService.findOwnerBookingsByState(userId, BookingState.WAITING, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findByOwnerAndStatus(eq(userId), any(BookingStatus.class), any(PageRequest.class));
                .findAllByItemOwnerAndStatus(eq(userId), any(BookingStatus.class), any(PageRequest.class));

        bookingService.findOwnerBookingsByState(userId, BookingState.REJECTED, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findByOwnerAndStatus(eq(userId), any(BookingStatus.class), any(PageRequest.class));
                .findAllByItemOwnerAndStatus(eq(userId), any(BookingStatus.class), any(PageRequest.class));

        bookingService.findOwnerBookingsByState(userId, BookingState.ALL, from, size);
        Mockito.verify(bookingRepository, Mockito.atLeastOnce())
                //.findByOwner(eq(userId), any(PageRequest.class));
                .findAllByItemOwner(eq(userId), any(PageRequest.class));
    }

}
