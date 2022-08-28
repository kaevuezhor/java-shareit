package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.CreateUserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(
            @RequestBody @Valid CreateUserDto userDto
    ) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(
            @PathVariable long userId
    ) {
        log.info("Get user id={}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable long userId,
            @RequestBody CreateUserDto patch
    ) throws ValidationException {
        if (notValid(patch)) {
            throw new ValidationException("Ошибка в теле запроса");
        }
        log.info("Patch user id={}, patch {}", userId, patch);
        return userClient.updateUser(userId, patch);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable long userId
    ) {
        log.info("Delete user id={}", userId);
        return userClient.deleteUser(userId);
    }

    private boolean notValid(CreateUserDto userDto) {
        boolean isBlankName = userDto.getName() != null && userDto.getName().isBlank();
        boolean isBlankEmail = userDto.getEmail() != null && userDto.getEmail().isBlank();
        return isBlankName || isBlankEmail;
    }
}
