package ru.practicum.shareit.user.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
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
    @SneakyThrows
    public UserDto getUser(int id) {
        try {
            return UserMapper.toUserDto(userDao.getUser(id));
        } catch (RuntimeException e) {
            throw new NotFoundException("Отсутсвует пользователь с id " + id);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @SneakyThrows
    public void deleteUser(int id) {
        try {
            userDao.deleteUser(id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Отсутсвует пользователь с id " + id);
        }
    }

    @Override
    @SneakyThrows
    public UserDto updateUser(int id, User user) {
        if (isExistingEmail(user)) {
            throw new AlreadyExistsException(
                    String.format("Пользователь с email %s уже существует", user.getEmail())
            );
        }
        try {
            return UserMapper.toUserDto(
                    userDao.updateUser(id, user)
            );
        } catch (RuntimeException e) {
            throw new NotFoundException("Отсутсвует пользователь с id " + id);
        }
    }

    @Override
    @SneakyThrows
    public UserDto createUser(User user) {
        if (isNotValidated(user)) {
            throw new ValidationException("Ошибка валидации");
        }
        if (isExistingEmail(user)) {
            throw new AlreadyExistsException(
                    String.format("Пользователь с email %s уже существует", user.getEmail())
            );
        }
        return UserMapper.toUserDto(userDao.createUser(user));
    }

    private boolean isExistingEmail(User user) {
        return userDao.getAllUsers()
                .stream()
                .map(User::getEmail)
                .anyMatch(e -> e.equals(user.getEmail()));
    }

    private boolean isNotValidated(User user) {
        boolean isBlankName = user.getName() == null;
        boolean isBlankEmail = user.getEmail() == null;
        return isBlankName || isBlankEmail;
    }
}
