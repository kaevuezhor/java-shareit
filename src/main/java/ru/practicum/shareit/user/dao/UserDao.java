package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserDao {
    User getUser(int id);

    List<User> getAllUsers();

    void deleteUser(int id);

    User updateUser(int id, User user);

    User createUser(User user);
}
