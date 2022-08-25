package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestServiceDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.RequestService;
import ru.practicum.shareit.requests.service.impl.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    private RequestService requestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    private User testUser;

    private Item testItem;

    private ItemRequest testRequest;


    private ItemRequest testRequestCreation;

    @BeforeEach
    void testSetup() {
        requestService = new RequestServiceImpl(userRepository, requestRepository, itemRepository);

        testUser = new User(
                1L,
                "name",
                "e@ma.il"
        );

        testRequest = new ItemRequest(
                1L,
                "need item",
                testUser,
                LocalDateTime.of(2022,1,1, 1,1)
        );

        testItem = new Item(
                1L,
                "item",
                "desc",
                true,
                1L,
                testRequest
        );

        testRequestCreation = new ItemRequest(
            "need item"
        );

        testRequestCreation.setCreated(LocalDateTime.of(2022,1,1, 1,1));
    }

    @Test
    void testCreateRequest() throws ValidationException, NotFoundException {
        long userId = 1;
        long wrongUserId = 3;
        String emptyDescription = "";
        ItemRequest wrongRequest = new ItemRequest(emptyDescription);
        wrongRequest.setCreated(LocalDateTime.of(2022,1,1, 1,1));

        Mockito
                .when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));

        ItemRequest itemRequestSavedByService = testRequestCreation;
        itemRequestSavedByService.setRequester(testUser);

        Mockito
                .when(requestRepository.save(itemRequestSavedByService))
                .thenReturn(testRequest);

        final NotFoundException notFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.createRequest(testRequestCreation, wrongUserId)
        );

        Assertions.assertEquals("Пользователь 3 не найден", notFoundException.getMessage());

        final ValidationException validationException = Assertions.assertThrows(
                ValidationException.class,
                () -> requestService.createRequest(wrongRequest, userId)
        );

        Assertions.assertEquals("Отсутствует описание", validationException.getMessage());

        ItemRequest savedRequest = requestService.createRequest(testRequestCreation, userId);

        Assertions.assertEquals(testRequest, savedRequest);
    }

    @Test
    void testFindById() throws Throwable {
        long userId = 1L;
        long requestId = 1L;
        long wrongUserId = 3L;
        long wrongRequestId = 3L;

        Mockito
                .when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        Mockito
                .when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        Mockito
                .when(requestRepository.findById(wrongRequestId))
                .thenReturn(Optional.empty());

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));

        Mockito
                .when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(testRequest));

        Mockito
                .when(itemRepository.findAllByRequestId(requestId))
                .thenReturn(List.of(testItem));

        final NotFoundException userNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findById(requestId, wrongUserId)
        );

        Assertions.assertEquals("Пользователь 3 не найден", userNotFoundException.getMessage());

        final NotFoundException requestNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findById(wrongRequestId, userId)
        );

        Assertions.assertEquals("Запрос 3 не найден", requestNotFoundException.getMessage());

        ItemRequestServiceDto expectedRequestServiceDto = new ItemRequestServiceDto(
                testRequest,
                List.of(testItem)
        );

        ItemRequestServiceDto foundRequestServiceDto = requestService.findById(requestId, userId);

        Assertions.assertEquals(expectedRequestServiceDto, foundRequestServiceDto);
    }

    @Test
    void testFindAll() throws ValidationException, NotFoundException {
        long userId = 1;
        long wrongUserId = 3;
        int from = 0;
        int size = 1;
        long requestId = 1;

        final ValidationException validationException1 = Assertions.assertThrows(
                ValidationException.class,
                () -> requestService.findAll(-1, 5, userId)
        );

        Assertions.assertEquals("Ошибка в параметрах запроса", validationException1.getMessage());

        final ValidationException validationException2 = Assertions.assertThrows(
                ValidationException.class,
                () -> requestService.findAll(1, 0, userId)
        );

        Assertions.assertEquals("Ошибка в параметрах запроса", validationException2.getMessage());

        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Order.desc("created")));

        Mockito
                .when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        final NotFoundException userNotFoundException = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findAll(from, size, wrongUserId)
        );

        Assertions.assertEquals("Пользователь 3 не найден", userNotFoundException.getMessage());

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));

        Mockito
                .when(requestRepository.findAllByRequesterIdNot(userId, pageRequest))
                .thenReturn(new PageImpl<>(List.of(testRequest)));

        Mockito
                .when(itemRepository.findAllByRequestId(requestId))
                .thenReturn(List.of(testItem));

        ItemRequestServiceDto expectedServiceDto = new ItemRequestServiceDto();
        expectedServiceDto.setItemRequest(testRequest);
        expectedServiceDto.setItems(List.of(testItem));

        List<ItemRequestServiceDto> foundServiceDtos = requestService.findAll(from, size, userId);

        Assertions.assertEquals(List.of(expectedServiceDto), foundServiceDtos);
    }
}
