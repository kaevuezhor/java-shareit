package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    List<Item> getSearchedItems(String text);

    List<Item> getAllItems(int userId);

    Item getItem(int id);

    Item updateItem(int itemId, Item item);

    Item createItem(Item item, int userId);

    Item deleteItem(int itemId);
}
