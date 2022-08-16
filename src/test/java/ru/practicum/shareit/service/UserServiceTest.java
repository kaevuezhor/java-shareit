package ru.practicum.shareit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User testUser1;

    private User testUser2;

    @BeforeEach
    void testSetup() {
        userService = new UserServiceImpl(userRepository);

        testUser1 = new User(
                1L,
                "name",
                "e@ma.il"
        );

        testUser2 = new User(
                2L,
                "eman",
                "il@ma.e"
        );
    }

    @Test
    void testGetUser() throws NotFoundException {
        long userId = 1L;
        long wrongId = 42L;

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser1));

        User foundUser = userService.getUser(userId);

        Assertions.assertEquals(testUser1, foundUser);

        final NotFoundException exception = Assertions.assertThrows(
          NotFoundException.class,
                () -> userService.getUser(wrongId)
        );

        Assertions.assertEquals("Пользователь id 42 не найден", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        List<User> usersFromDb = List.of(testUser1, testUser2);

        Mockito
                .when(userRepository.findAll())
                .thenReturn(usersFromDb);

        List<User> foundUsers = userService.getAllUsers();

        Assertions.assertEquals(usersFromDb, foundUsers);
    }

    @Test
    void testCreateUser() throws AlreadyExistsException {
        User createdUser = new User(
                null,
                "name",
                "e@ma.il"
        );

        Mockito
                .when(userRepository.save(createdUser))
                .thenReturn(testUser1);

        User savedUser = userService.createUser(createdUser);

        Assertions.assertEquals(savedUser, testUser1);

        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(testUser1));

        final AlreadyExistsException exception = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> userService.createUser(createdUser)
        );

        Assertions.assertEquals("Пользователь с email e@ma.il уже существует", exception.getMessage());
    }

    @Test
    void testUpdateUser() throws AlreadyExistsException, NotFoundException {
        User pathingUser = new User(
                null,
                "other name",
                null
        );
        long id = 1L;
        long wrongId = 3L;
        User expectedUser = testUser1;
        expectedUser.setName(pathingUser.getName());

        Mockito
                .when(userRepository.save(expectedUser))
                .thenReturn(expectedUser);

        Mockito
                .when(userRepository.findById(id))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(userRepository.findById(wrongId))
                .thenReturn(Optional.empty());

        Mockito
                .when(userRepository.getReferenceById(id))
                .thenReturn(testUser1);

        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(testUser1, testUser2));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(wrongId, pathingUser)
        );

        Assertions.assertEquals("Пользователь 3 не найден", exception.getMessage());

        User updatedUser = userService.updateUser(id, pathingUser);

        Assertions.assertEquals(expectedUser, updatedUser);
    }
}
