package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;

/**
 * // TODO .
 */

@Data
public class ItemDto {
    private final String name;
    private final String description;
    private final boolean available;
    private final Integer request;
}
