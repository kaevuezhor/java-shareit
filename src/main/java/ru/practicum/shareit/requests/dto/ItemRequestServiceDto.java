package ru.practicum.shareit.requests.dto;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class ItemRequestServiceDto {
    private ItemRequest itemRequest;
    private List<Item> items;
}
