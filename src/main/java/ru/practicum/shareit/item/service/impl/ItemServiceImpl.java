package ru.practicum.shareit.item.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemDao.getSearchedItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllUserItems(int userId) {
        return itemDao.getAllItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDto> getItem(int itemId) {
        return Optional.of(ItemMapper.toItemDto(itemDao.getItem(itemId)));
    }

    @Override
    public ItemDto createItem(Item item, int userId) throws NotFoundException {
        if (userDao.getUser(userId) == null) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        return ItemMapper.toItemDto(itemDao.createItem(item, userId));
    }

    @Override
    public Optional<ItemDto> updateItem(int itemId, Item item, int userId) throws NotFoundException, AccessException {
        if (userDao.getUser(userId) == null) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        if (isNotOwner(itemId, userId)) {
            throw new AccessException(
                    String.format("Пользователь %s не является владельцем предмета %s", userId, itemId)
            );
        }
        return Optional.of(ItemMapper.toItemDto(itemDao.updateItem(itemId, item)));
    }

    @Override
    public Optional<ItemDto> deleteItem(int itemId, int userId) throws AccessException, NotFoundException {
        if (userDao.getUser(userId) == null) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        if (isNotOwner(itemId, userId)) {
            throw new AccessException(
                    String.format("Пользователь %s не является владельцем предмета %s", userId, itemId)
            );
        }
        return Optional.of(ItemMapper.toItemDto(itemDao.deleteItem(itemId)));
    }


    private boolean isNotOwner(int itemId, int userId) {
        return itemDao.getItem(itemId).getOwner() != userId;
    }
}
