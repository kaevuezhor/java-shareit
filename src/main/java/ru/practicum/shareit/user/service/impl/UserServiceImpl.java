package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(long id) throws NotFoundException {
        Optional<User> foundUser = userRepository.findById(id);
        if (foundUser.isEmpty()) {
            throw new NotFoundException("Пользователь id " + id + " не найден");
        }
        return foundUser.get();
    }

    @Override
    public User createUser(User user) throws AlreadyExistsException {
        if (isExistingEmail(user)) {
            throw new AlreadyExistsException(
                    String.format("Пользователь с email %s уже существует", user.getEmail())
            );
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(long id, User user) throws AlreadyExistsException {
        if (isExistingEmail(user)) {
            throw new AlreadyExistsException(
                    String.format("Пользователь с email %s уже существует", user.getEmail())
            );
        }
        return patch(user, userRepository.getReferenceById(id));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    private boolean isExistingEmail(User user) {
        return userRepository.findAll().stream()
                .map(User::getEmail)
                .anyMatch(e -> e.equals(user.getEmail()));
    }

    private User patch(User patch, User user) {
        if (patch.getName() != null) {
            user.setName(patch.getName());
        }
        if (patch.getEmail() != null) {
            user.setEmail(patch.getEmail());
        }
        return userRepository.save(user);
    }
}
