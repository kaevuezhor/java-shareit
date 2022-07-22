package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<Item> searchItems(String text);

    List<Item> getAllUserItems(long userId);

    Item getItem(long id) throws NotFoundException;

    Item createItem(Item item, long userId) throws NotFoundException;

    Item updateItem(long itemId, Item item, long userId) throws NotFoundException, AccessException;

    void deleteItem(long itemId, long userId) throws AccessException, NotFoundException;
}
