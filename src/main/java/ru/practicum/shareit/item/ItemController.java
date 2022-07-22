package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

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
        return itemService.searchItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader ("X-Sharer-User-Id") int userId) {
        log.info("Запрошены все предметы пользователя {}", userId);
        return itemService.getAllUserItems(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) throws NotFoundException {
        log.info("Запрошен предмет id {}", itemId);
        return ItemMapper.toItemDto(itemService.getItem(itemId));
    }

    @PostMapping
    public ItemDto createItem(@RequestBody Item item,
                              @RequestHeader ("X-Sharer-User-Id") int userId
    ) throws ValidationException, NotFoundException {
        if (isNotValidated(item)) {
            throw new ValidationException("Ошибка валидации");
        }
        log.info("Создан предмет {} пользователем {}", item, userId);
        return ItemMapper.toItemDto(itemService.createItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestBody Item item,
                              @RequestHeader ("X-Sharer-User-Id") int userId
    ) throws NotFoundException, AccessException {
        log.info("Обновлен предмет {} пользователем {},\n"
        + "Обновлено: {}", itemId, userId, item);
        return ItemMapper.toItemDto(itemService.updateItem(itemId, item, userId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId,
                           @RequestHeader ("X-Sharer-User-Id") int userId
    ) throws AccessException, NotFoundException {
        log.info("Удален предмет {} пользователем {}", itemId, userId);
        itemService.deleteItem(itemId, userId);
    }

    private boolean isNotValidated(Item item) {
        boolean isBlankName = !StringUtils.hasText(item.getName());
        boolean isBlankDescription = !StringUtils.hasText(item.getDescription());
        boolean isBlankAvailable = item.getAvailable() == null;
        return isBlankName || isBlankDescription || isBlankAvailable;
    }
}
