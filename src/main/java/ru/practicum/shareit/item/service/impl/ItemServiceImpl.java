package ru.practicum.shareit.item.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
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
        return itemDao.getAllItems().stream()
                .filter(i -> i.getOwner() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @SneakyThrows
    public ItemDto getItem(int itemId) {
        try {
            return ItemMapper.toItemDto(itemDao.getItem(itemId));
        } catch (RuntimeException e) {
            throw new NotFoundException("Отсутсвует предмет с id " + itemId);
        }
    }

    @Override
    @SneakyThrows
    public ItemDto createItem(Item item, int userId) {
        try {
            userDao.getUser(userId);
            log.info(userDao.getUser(userId).toString());
        } catch (RuntimeException e) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        if (isNotValidated(item)) {
            throw new ValidationException("Ошибка валидации");
        }
        return ItemMapper.toItemDto(itemDao.createItem(item, userId));
    }

    @Override
    @SneakyThrows
    public ItemDto updateItem(int itemId, Item item, int userId) {
        try {
            userDao.getUser(userId);
        } catch (RuntimeException e) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        if (isNotOwner(itemId, userId)) {
            throw new AccessException(
                    String.format("Пользователь %s не является владельцем предмета %s", userId, itemId)
            );
        }
        try {
            return ItemMapper.toItemDto(
                    itemDao.updateItem(itemId, item)
            );
        } catch (RuntimeException e) {
            throw new NotFoundException("Отсутсвует предмет с id " + itemId);
        }
    }

    @Override
    @SneakyThrows
    public void deleteItem(int itemId, int userId) {
        try {
            userDao.getUser(userId);
        } catch (RuntimeException e) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        if (isNotOwner(itemId, userId)) {
            throw new AccessException(
                    String.format("Пользователь %s не является владельцем предмета %s", userId, itemId)
            );
        }
        try {
            itemDao.deleteItem(itemId);
        } catch (RuntimeException e) {
            throw new NotFoundException("Отсутсвует предмет с id " + itemId);
        }
    }

    private boolean isNotValidated(Item item) {
        boolean isBlankName = item.getName() == null || item.getName().isBlank();
        boolean isBlankDescription = item.getDescription() == null || item.getDescription().isBlank();
        boolean isBlankAvailable = item.getAvailable() == null;
        return isBlankName || isBlankDescription || isBlankAvailable;
    }

    private boolean isNotOwner(int itemId, int userId) {
        return itemDao.getItem(itemId).getOwner() != userId;
    }
}
