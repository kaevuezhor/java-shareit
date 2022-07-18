package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDto> searchItems(String text);

    List<ItemDto> getAllUserItems(int userId);

    Optional<ItemDto> getItem(int id);

    ItemDto createItem(Item item, int userId) throws NotFoundException;

    Optional<ItemDto> updateItem(int itemId, Item item, int userId) throws NotFoundException, AccessException;

    Optional<ItemDto> deleteItem(int itemId, int userId) throws AccessException, NotFoundException;
}
