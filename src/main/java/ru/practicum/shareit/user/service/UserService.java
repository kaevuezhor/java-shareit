package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto getUser(int id);

    List<UserDto> getAllUsers();

    void deleteUser(int id);

    UserDto updateUser(int id, User user);

    UserDto createUser(User user);
}
