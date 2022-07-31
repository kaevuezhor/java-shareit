package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
        return UserMapper.toUserDto(userService.getUser(id));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) throws ValidationException, AlreadyExistsException {
        if (isNotValidated(user)) {
            throw new ValidationException("Ошибка валидации");
        }
        log.info("Создан пользователь {}", user);
        return UserMapper.toUserDto(userService.createUser(user));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable int id,
                              @Valid @RequestBody User user
    ) throws AlreadyExistsException, NotFoundException {
        log.info("Обновлен пользователь id {}", id);
        return UserMapper.toUserDto(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id){
        log.info("Удалён пользователь id {}", id);
        userService.deleteUser(id);
    }

    private boolean isNotValidated(User user) {
        boolean isBlankName = !StringUtils.hasText(user.getName());
        boolean isBlankEmail = !StringUtils.hasText(user.getEmail());
        return isBlankName || isBlankEmail;
    }
}
