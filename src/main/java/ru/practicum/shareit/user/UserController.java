package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable int id) throws NotFoundException {
        log.info("Запрошен пользователь id {}", id);
        Optional<UserDto> foundUser = userService.getUser(id);
        if (foundUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return foundUser.get();
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) throws ValidationException, AlreadyExistsException {
        if (isNotValidated(user)) {
            throw new ValidationException("Ошибка валидации");
        }
        log.info("Создан пользователь {}", user);
        return userService.createUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable int id,
                              @Valid @RequestBody User user
    ) throws AlreadyExistsException, NotFoundException {
        log.info("Обновлен пользователь id {}", id);
        Optional<UserDto> updatedUser = userService.updateUser(id, user);
        if (updatedUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return updatedUser.get();
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable int id) throws NotFoundException {
        log.info("Удалён пользователь id {}", id);
        Optional<UserDto> deletedUser = userService.deleteUser(id);
        if (deletedUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return deletedUser.get();
    }

    private boolean isNotValidated(User user) {
        boolean isBlankName = !StringUtils.hasText(user.getName());
        boolean isBlankEmail = !StringUtils.hasText(user.getEmail());
        return isBlankName || isBlankEmail;
    }
}
