package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.dto.ItemDtoUserView;
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
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<ItemDtoUserView> getAllItems(@RequestHeader ("X-Sharer-User-Id") int userId) {
        log.info("Запрошены все предметы пользователя {}", userId);
        List<ItemDtoService> foundItems = itemService.getAllUserItems(userId);
        return foundItems.stream()
                .map(itemMapper::toItemDtoUserView)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDtoUserView getItem(
            @PathVariable int itemId,
            @RequestHeader ("X-Sharer-User-Id") int userId
    ) throws NotFoundException {
        log.info("Запрошен предмет id {}", itemId);
        ItemDtoService foundItem = itemService.getItem(itemId, userId);
        return itemMapper.toItemDtoUserView(foundItem);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody Item item,
                              @RequestHeader ("X-Sharer-User-Id") int userId
    ) throws ValidationException, NotFoundException {
        if (isNotValidated(item)) {
            throw new ValidationException("Ошибка валидации");
        }
        log.info("Создан предмет {} пользователем {}", item, userId);
        return itemMapper.toItemDto(itemService.createItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestBody Item item,
                              @RequestHeader ("X-Sharer-User-Id") int userId
    ) throws NotFoundException, AccessException {
        log.info("Обновлен предмет {} пользователем {},\n"
        + "Обновлено: {}", itemId, userId, item);
        return itemMapper.toItemDto(itemService.updateItem(itemId, item, userId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId,
                           @RequestHeader ("X-Sharer-User-Id") int userId
    ) throws AccessException, NotFoundException {
        log.info("Удален предмет {} пользователем {}", itemId, userId);
        itemService.deleteItem(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @RequestHeader ("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody Comment comment
    ) throws ValidationException, NotFoundException, AccessException, NotBookedException {
        log.info("Оставлен комментарий к предмету {} пользователем {}", itemId, userId);
        if (isNotValidated(comment)) {
            throw new ValidationException("Ошибка валидации");
        }
        return itemMapper.toCommentDto(itemService.postComment(userId, itemId, comment));
    }

    private boolean isNotValidated(Item item) {
        boolean isBlankName = !StringUtils.hasText(item.getName());
        boolean isBlankDescription = !StringUtils.hasText(item.getDescription());
        boolean isBlankAvailable = item.getAvailable() == null;
        return isBlankName || isBlankDescription || isBlankAvailable;
    }

    private boolean isNotValidated(Comment comment) {
        boolean isBlankText = !StringUtils.hasText(comment.getText());
        return isBlankText;
    }
}
