package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private final long id;
    private final String description;
    private final LocalDateTime created;
    private final List<ItemDto> items;
}
