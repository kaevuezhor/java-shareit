package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    private Item testItem;

    private User testUser1;

    private User testUser2;
    private ItemRequest testRequest;

    @BeforeEach
    void testSetup() {
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                requestRepository
        );
        testItem = new Item(
                1L,
                "item",
                "desc",
                true,
                1L,
                testRequest
        );
        testUser1 = new User(
                1L, "name", "e@ma.il"
        );
        testUser2 = new User(
                2L, "2name", "e2@ma.il"
        );
        testRequest = new ItemRequest(
                1L,
                "need item",
                testUser1,
                LocalDateTime.of(2022,1,1,1,1)
        );
    }

    @Test
    void testSearch() {
        String searchText = "sc";
        int from = 0;
        int size = 1;

        Mockito
                .when(itemRepository.search(searchText, PageRequest.of(from, size, Sort.by(Sort.Order.asc("id")))))
                .thenReturn(List.of(testItem));

        List<Item> foundItems = itemService.searchItems(searchText, from,size);

        Assertions.assertEquals(List.of(testItem), foundItems);

        foundItems = itemService.searchItems("", from, size);

        Assertions.assertEquals(List.of(), foundItems);
    }

    @Test
    void testGetAll() {
        long userId = 1L;
        int from = 0;
        int size = 1;

        Mockito
                .when(itemRepository.findByOwner(userId, PageRequest.of(from, size, Sort.by(Sort.Order.asc("id")))))
                .thenReturn(List.of(testItem));

        Mockito
                .when(bookingRepository.findByItemIdOrderByStart(testItem.getId()))
                .thenReturn(List.of());

        Mockito
                .when(commentRepository.findAllByItemId(testItem.getId()))
                .thenReturn(List.of());

        ItemDtoService testids = new ItemDtoService(testItem, List.of(), List.of());

        List<ItemDtoService> foundItems = itemService.getAllUserItems(userId, from, size);

        Assertions.assertEquals(List.of(testids), foundItems);
    }

    @Test
    void testGetItem() throws NotFoundException {
        long itemId = 1;
        long userId = 1;

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(testItem));

        ItemDtoService expected = new ItemDtoService(testItem, List.of(), List.of());
        ItemDtoService foundItem = itemService.getItem(itemId, userId);

        Assertions.assertEquals(expected, foundItem);
    }

    @Test
    void testCreateItem() throws NotFoundException {
        long userId = 1L;
        long requestId = 1L;

        ItemDtoCreated requestBody = new ItemDtoCreated(
                "item",
                "desc",
                true,
                requestId
        );

        Mockito
                .when(requestRepository.getReferenceById(requestId))
                .thenReturn(testRequest);

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser1));

        Item savingItem = new Item(
                null,
                requestBody.getName(),
                requestBody.getDescription(),
                requestBody.getAvailable(),
                userId,
                testRequest);

        Mockito.when(itemRepository.save(savingItem))
                .thenReturn(testItem);

        Item savedItem = itemService.createItem(requestBody, userId);

        Assertions.assertEquals(testItem, savedItem);
    }

    @Test
    void testUpdateItem() throws AccessException, NotFoundException {
        long itemId = 1L;
        long userId = 1L;
        long notOwnerUserId = 2L;
        long wrongUserId = 6L;

        Item itemPatch = new Item(
                null,
                "patch",
                null,
                null,
                null,
                null
        );

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser1));
        Mockito
                .when(userRepository.findById(notOwnerUserId))
                .thenReturn(Optional.of(testUser2));
        Mockito
                .when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());
        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(itemRepository.getReferenceById(itemId))
                .thenReturn(testItem);

        final NotFoundException userNotFoundException = Assertions.assertThrows(
            NotFoundException.class,
                () -> itemService.updateItem(itemId, itemPatch, wrongUserId)
        );

        Assertions.assertEquals("Пользователь 6 не найден", userNotFoundException.getMessage());

        final AccessException userAccessException = Assertions.assertThrows(
                AccessException.class,
                () -> itemService.updateItem(itemId, itemPatch, notOwnerUserId)
        );

        Assertions.assertEquals("Пользователь 2 не является владельцем предмета 1", userAccessException.getMessage());

        Item patchedItem = testItem;
        patchedItem.setName(itemPatch.getName());

        Mockito.when(itemRepository.save(patchedItem))
                .thenReturn(patchedItem);

        Assertions.assertEquals(patchedItem, itemService.updateItem(itemId, itemPatch, userId));
    }

    @Test
    void testPostComment() throws AccessException, NotFoundException, NotBookedException {
        long userId = 1L;
        long itemId = 1L;
        long wrongUserId = 6L;
        long wrongItemId = 6L;

        Comment requestBody = new Comment(
                null,
                "comment",
                null,
                null,
                LocalDateTime.of(2022,1,2,1,0)
        );
        Comment expectedComment = new Comment(
                1L,
                "comment",
                testItem,
                testUser1,
                LocalDateTime.of(2022,1,2,1,0)
        );
        Comment savingComment = requestBody;
        savingComment.setAuthor(testUser1);
        savingComment.setItem(testItem);

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(testItem));

        Mockito
                .when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        Mockito
                .when(itemRepository.findById(wrongItemId))
                .thenReturn(Optional.empty());

        Mockito
                .when(bookingRepository
                        //.findUserPast(eq(userId), any(LocalDateTime.class)))
                        .findAllByBookerIdAndEndBefore(eq(userId), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking(
                        1L,
                        LocalDateTime.of(2022,1,1,1,1),
                        LocalDateTime.of(2022,1,1,2,1),
                        testItem,
                        testUser1,
                        BookingStatus.APPROVED
                )));

        Mockito
                .when(commentRepository.save(savingComment))
                .thenReturn(expectedComment);

        final NotFoundException userNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.postComment(wrongUserId, itemId, requestBody)
        );

        Assertions.assertEquals("Пользователь 6 не найден", userNotFoundException.getMessage());

        final NotFoundException itemNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.postComment(userId, wrongItemId, requestBody)
        );

        Assertions.assertEquals("Предмет 6 не найден", itemNotFoundException.getMessage());

        Comment savedComment = itemService.postComment(userId, itemId, requestBody);

        Assertions.assertEquals(expectedComment, savedComment);
    }

}
