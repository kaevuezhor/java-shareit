package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserDto> getUser(int id);

    List<UserDto> getAllUsers();

    Optional<UserDto> deleteUser(int id);

    Optional<UserDto> updateUser(int id, User user) throws AlreadyExistsException;

    UserDto createUser(User user) throws AlreadyExistsException;
}
