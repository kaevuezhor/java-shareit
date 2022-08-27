package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam String text
    ) {
        log.info("Search items text={}, from={}, size={}", text, from, size);
        return itemService.searchItems(text, from, size)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<ItemDtoUserView> getUserItems(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestHeader ("X-Sharer-User-Id") long userId
    ) {
        log.info("Get items userId={}, from={}, size={}", userId, from, size);
        List<ItemDtoService> foundItems = itemService.getAllUserItems(userId, from, size);
        return foundItems.stream()
                .map(itemMapper::toItemDtoUserView)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDtoUserView getItem(
            @PathVariable int itemId,
            @RequestHeader ("X-Sharer-User-Id") long userId
    ) throws NotFoundException {
        log.info("Get item id={} by user id={}", itemId, userId);
        ItemDtoService foundItem = itemService.getItem(itemId, userId);
        return itemMapper.toItemDtoUserView(foundItem);
    }

    @PostMapping
    public ItemDto createItem(
            @RequestBody ItemDtoCreated item,
            @RequestHeader ("X-Sharer-User-Id") long userId
    ) throws NotFoundException {
        log.info("Create item {} by user id={}", item, userId);
        return itemMapper.toItemDto(itemService.createItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable long itemId,
            @RequestBody Item item,
            @RequestHeader ("X-Sharer-User-Id") long userId
    ) throws NotFoundException, AccessException {
        log.info("Обновлен предмет {} пользователем {},\n"
        + "Обновлено: {}", itemId, userId, item);
        return itemMapper.toItemDto(itemService.updateItem(itemId, item, userId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @PathVariable long itemId,
            @RequestHeader ("X-Sharer-User-Id") long userId
    ) throws AccessException, NotFoundException {
        log.info("Delete item id={} by user id={}", itemId, userId);
        itemService.deleteItem(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @RequestHeader ("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody Comment comment
    ) throws NotFoundException, AccessException, NotBookedException {
        log.info("Post comment {} to item id={} by user id={}", comment, itemId, userId);
        return itemMapper.toCommentDto(itemService.postComment(userId, itemId, comment));
    }
}
