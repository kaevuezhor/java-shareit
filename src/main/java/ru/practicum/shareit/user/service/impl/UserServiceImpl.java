package ru.practicum.shareit.user.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getUser(int id) {
        return Optional.of(UserMapper.toUserDto(userDao.getUser(id)));
    }

    @Override
    public UserDto createUser(User user) throws AlreadyExistsException {
        if (isExistingEmail(user)) {
            throw new AlreadyExistsException(
                    String.format("Пользователь с email %s уже существует", user.getEmail())
            );
        }
        return UserMapper.toUserDto(userDao.createUser(user));
    }

    @Override
    public Optional<UserDto> updateUser(int id, User user) throws AlreadyExistsException {
        if (isExistingEmail(user)) {
            throw new AlreadyExistsException(
                    String.format("Пользователь с email %s уже существует", user.getEmail())
            );
        }
        return Optional.of(UserMapper.toUserDto(userDao.updateUser(id, user)));
    }

    @Override
    public Optional<UserDto> deleteUser(int id) {
        return Optional.of(UserMapper.toUserDto(userDao.deleteUser(id)));
    }

    private boolean isExistingEmail(User user) {
        return userDao.getAllUsers().stream()
                .map(User::getEmail)
                .anyMatch(e -> e.equals(user.getEmail()));
    }
}
