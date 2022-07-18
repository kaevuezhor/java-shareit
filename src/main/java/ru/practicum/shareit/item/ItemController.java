package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader ("X-Sharer-User-Id") int userId) {
        log.info("Запрошены все предметы");
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) throws NotFoundException {
        log.info("Запрошен предмет id {}", itemId);
        Optional<ItemDto> foundItem = itemService.getItem(itemId);
        if (foundItem.isEmpty()) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
        return foundItem.get();
    }

    @PostMapping
    public ItemDto createItem(@RequestBody Item item,
                              @RequestHeader ("X-Sharer-User-Id") int userId
    ) throws ValidationException, NotFoundException {
        if (isNotValidated(item)) {
            throw new ValidationException("Ошибка валидации");
        }
        log.info("Создан предмет {} пользователем {}", item, userId);
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestBody Item item,
                              @RequestHeader ("X-Sharer-User-Id") int userId
    ) throws NotFoundException, AccessException {
        log.info("Обновлен предмет {} пользователем {},\n"
        + "Обновлено: {}", itemId, userId, item);
        Optional<ItemDto> updatedItem = itemService.updateItem(itemId, item, userId);
        if (updatedItem.isEmpty()) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
        return updatedItem.get();
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@PathVariable int itemId,
                           @RequestHeader ("X-Sharer-User-Id") int userId
    ) throws AccessException, NotFoundException {
        log.info("Удален предмет {} пользователем {}", itemId, userId);
        Optional<ItemDto> deletedItem = itemService.deleteItem(itemId, userId);
        if (deletedItem.isEmpty()) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
        return deletedItem.get();
    }

    private boolean isNotValidated(Item item) {
        boolean isBlankName = !StringUtils.hasText(item.getName());
        boolean isBlankDescription = !StringUtils.hasText(item.getDescription());
        boolean isBlankAvailable = item.getAvailable() == null;
        return isBlankName || isBlankDescription || isBlankAvailable;
    }
}
