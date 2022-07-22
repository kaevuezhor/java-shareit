package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User getUser(long id) throws NotFoundException;

    List<User> getAllUsers();

    void deleteUser(long id);

    User updateUser(long id, User user) throws AlreadyExistsException;

    User createUser(User user) throws AlreadyExistsException;
}
