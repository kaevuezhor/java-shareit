package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {

    List<Item> searchItems(String text);

    List<ItemDtoService> getAllUserItems(long userId);

    ItemDtoService getItem(long itemId, long userId) throws NotFoundException;

    Item createItem(Item item, long userId) throws NotFoundException;

    Item updateItem(long itemId, Item item, long userId) throws NotFoundException, AccessException;

    void deleteItem(long itemId, long userId) throws AccessException, NotFoundException;

    Comment postComment(long userId, long itemId, Comment comment) throws NotFoundException, AccessException, NotBookedException;
}
