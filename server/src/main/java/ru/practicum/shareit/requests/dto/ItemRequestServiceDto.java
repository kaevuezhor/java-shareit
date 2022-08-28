package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestServiceDto {
    private ItemRequest itemRequest;
    private List<Item> items;
}
