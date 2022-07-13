package ru.practicum.shareit.requests.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
public class ItemRequestDto {
    private final String description;
    private final User requester;
    private final LocalDateTime created;
}
