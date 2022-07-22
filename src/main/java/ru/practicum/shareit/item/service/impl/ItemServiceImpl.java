package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(text, text);
    }

    @Override
    public List<Item> getAllUserItems(long userId) {
        return itemRepository.findByOwner(userId);
    }

    @Override
    public Item getItem(long itemId) throws NotFoundException {
        Optional<Item> foundItem = itemRepository.findById(itemId);
        if (foundItem.isEmpty()) {
            throw new NotFoundException("Предмет с id " + itemId + "не найден");
        }
        return foundItem.get();
    }

    @Override
    public Item createItem(Item item, long userId) throws NotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        item.setOwner(userId);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(long itemId, Item item, long userId) throws NotFoundException, AccessException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        if (isNotOwner(itemId, userId)) {
            throw new AccessException(
                    String.format("Пользователь %s не является владельцем предмета %s", userId, itemId)
            );
        }
        return patchItem(item, itemRepository.getReferenceById(itemId));
    }

    @Override
    public void deleteItem(long itemId, long userId) throws AccessException, NotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Отсутсвует пользователь с id " + userId);
        }
        if (isNotOwner(itemId, userId)) {
            throw new AccessException(
                    String.format("Пользователь %s не является владельцем предмета %s", userId, itemId)
            );
        }
        itemRepository.deleteById(itemId);
    }


    private boolean isNotOwner(long itemId, long userId) {
        return itemRepository.getReferenceById(itemId).getOwner() != userId;
    }

    private Item patchItem(Item patch, Item item) {
        if (patch.getName() != null) {
            item.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            item.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }
        if (patch.getRequest() != null) {
            item.setRequest(patch.getRequest());
        }
        return itemRepository.save(item);
    }
}
