package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> searchItems(String text, int from, int size);

    List<ItemDtoService> getAllUserItems(long userId, int from, int size);

    ItemDtoService getItem(long itemId, long userId) throws NotFoundException;

    Item createItem(ItemDtoCreated item, long userId) throws NotFoundException;

    Item updateItem(long itemId, Item item, long userId) throws NotFoundException, AccessException;

    void deleteItem(long itemId, long userId) throws AccessException, NotFoundException;

    Comment postComment(long userId, long itemId, Comment comment) throws NotFoundException, AccessException, NotBookedException;
}
