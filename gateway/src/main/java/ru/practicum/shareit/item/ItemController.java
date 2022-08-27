package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestBody @Valid CreateItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("Create item {} by user id={}", itemDto, userId);
        return itemClient.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @PathVariable long itemId,
            @RequestHeader ("X-Sharer-User-Id") long userId
    ) {
        log.info("Get item id={} by user id={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size,
            @RequestHeader ("X-Sharer-User-Id") long userId
    ) {
        log.info("Get items userId={}, from={}, size={}", userId, from, size);
        return itemClient.getUserItems(from, size, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @PathVariable long itemId,
            @RequestBody CreateItemDto patch,
            @RequestHeader ("X-Sharer-User-Id") long userId
    ) throws ValidationException {
        if (notValid(patch)) {
            throw new ValidationException("Ошибка в теле запроса");
        }
        log.info("Update item {}, id={} by user id={}", patch, itemId, userId);
        return itemClient.updateItem(itemId, patch, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @PathVariable long itemId,
            @RequestHeader ("X-Sharer-User-Id") long userId
    ) {
        log.info("Delete item id={} by user id={}", itemId, userId);
        return itemClient.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam String text
    ) {
        log.info("Search items text={}, from={}, size={}", text, from, size);
        return itemClient.searchItems(from, size,text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            @PathVariable long itemId,
            @RequestBody @Valid CreateCommentDto commentDto,
            @RequestHeader ("X-Sharer-User-Id") long userId
    ) {
        log.info("Post comment {} to item id={} by user id={}", commentDto, itemId, userId);
        return itemClient.postComment(itemId, commentDto, userId);
    }

    private boolean notValid(CreateItemDto itemDto) {
        boolean isBlankName = itemDto.getName() != null && itemDto.getName().isBlank();
        boolean isBlankDescription = itemDto.getDescription() != null && itemDto.getDescription().isBlank();
        return isBlankName || isBlankDescription;
    }
}
