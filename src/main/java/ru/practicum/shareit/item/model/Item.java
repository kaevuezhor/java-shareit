package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * // TODO .
 */
@Data
public class Item {
    private final int id;
    private final String name;
    private final String description;
    private final boolean available;
    private final User owner;
    private final ItemRequest request;
}
