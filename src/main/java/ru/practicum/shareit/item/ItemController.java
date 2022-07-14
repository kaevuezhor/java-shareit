package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

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
    public ItemDto getItem(@PathVariable int itemId) {
        log.info("Запрошен предмет id " + itemId);
        return itemService.getItem(itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody Item item,
                              @RequestHeader ("X-Sharer-User-Id") int userId
    ) {
        log.info("Создан предмет " + item + " пользователем " + userId);
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestBody Item item,
                              @RequestHeader ("X-Sharer-User-Id") int userId
    ){
        log.info("Обновлен предмет " + itemId + " пользователем " + userId + ",\n"
        + "Обновлено: " + item);
        return itemService.updateItem(itemId, item, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId,
                           @RequestHeader ("X-Sharer-User-Id") int userId
    ) {
        log.info("Удален предмет " + itemId + " пользователем " + userId);
        itemService.deleteItem(itemId, userId);
    }
}
