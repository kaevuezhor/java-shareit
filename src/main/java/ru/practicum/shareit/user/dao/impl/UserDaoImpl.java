package ru.practicum.shareit.user.dao.impl;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDaoImpl implements UserDao {

    private final Map<Integer, User> userStorage = new HashMap<>();

    private static int nextId = 1;

    @Override
    public User getUser(int id) {
        return userStorage.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public void deleteUser(int id) {
        userStorage.remove(id);
    }

    @Override
    public User updateUser(int id, User user) {
        user.setId(id);
        userStorage.put(id, patch(user));
        return userStorage.get(id);
    }

    @Override
    @SneakyThrows
    public User createUser(User user) {
        user.setId(nextId++);
        userStorage.put(user.getId(), user);
        return userStorage.get(user.getId());
    }

    private User patch(User user) {
        User savedUser = userStorage.get(user.getId());
        if (user.getEmail() != null) {
            savedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            savedUser.setName(user.getName());
        }
        return savedUser;
    }
}
