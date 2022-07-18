package ru.practicum.shareit.item.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemDaoImpl implements ItemDao {

    private final Map<Integer, Item> itemStorage = new HashMap<>();
    private static int nextId = 1;

    @Override
    public List<Item> getSearchedItems(String text) {
        return itemStorage.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItems(int userId) {
        return itemStorage.values().stream()
                .filter(i -> i.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItem(int itemId) {
        return itemStorage.get(itemId);
    }

    @Override
    public Item updateItem(int itemId, Item item) {
        item.setId(itemId);
        itemStorage.put(itemId, patch(item));
        return itemStorage.get(itemId);
    }

    @Override
    public Item createItem(Item item, int userId) {
        item.setId(nextId++);
        item.setOwner(userId);
        itemStorage.put(item.getId(), item);
        return itemStorage.get(item.getId());
    }

    @Override
    public Item deleteItem(int itemId) {
        Item deletedItem = itemStorage.get(itemId);
        itemStorage.remove(itemId);
        return deletedItem;
    }

    private Item patch(Item item) {
        Item savedItem = itemStorage.get(item.getId());
        if (item.getName() != null) {
            savedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            savedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            savedItem.setAvailable(item.getAvailable());
        }
        return savedItem;
    }
}
