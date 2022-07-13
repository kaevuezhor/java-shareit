package ru.practicum.shareit.requests;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
public class ItemRequest {
    private final int id;
    private final String description;
    private final User requester;
    private final LocalDateTime created;
}
