package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private long usersId = 1;
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
        long id = usersId++;
        user.setId(id);
        if (isExistingEmail(user)) {
            throw new AlreadyExistsException(
                    String.format("Пользователь с email %s уже существует", user.getEmail())
            );
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(long id, User user) throws AlreadyExistsException, NotFoundException {
        if (isExistingEmail(user)) {
            throw new AlreadyExistsException(
                    String.format("Пользователь с email %s уже существует", user.getEmail())
            );
        }
        Optional<User> savedUser = userRepository.findById(id);
        if (savedUser.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь %s не найден", id));
        }
        User updatedUser = patch(user, userRepository.getReferenceById(id));
        return userRepository.save(updatedUser);
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
        return user;
    }
}
