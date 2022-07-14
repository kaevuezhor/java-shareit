package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> searchItems(String text);

    List<ItemDto> getAllUserItems(int userId);

    ItemDto getItem(int id);

    ItemDto createItem(Item item, int userId);

    ItemDto updateItem(int itemId, Item item, int userId);

    void deleteItem(int itemId, int userId);
}
