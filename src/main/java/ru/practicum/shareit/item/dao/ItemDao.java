package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    List<Item> getSearchedItems(String text);

    List<Item> getAllItems();

    Item getItem(int id);

    Item updateItem(int itemId, Item item);

    Item createItem(Item item, int userId);

    void deleteItem(int itemId);
}
