package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User getUser(int id);

    List<User> getAllUsers();

    User deleteUser(int id);

    User updateUser(int id, User user);

    User createUser(User user);
}
