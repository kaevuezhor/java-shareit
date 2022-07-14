package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;

@Data
@AllArgsConstructor
public class Item {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private int owner;
    private ItemRequest request;
}
