package ru.practicum.shareit.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestServiceDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.RequestService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestMapper requestMapper;
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(
            @RequestBody ItemRequest request,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) throws NotFoundException, ValidationException {
        ItemRequest createdRequest = requestService.createRequest(request, userId);
        log.info("Создать запрос {}", createdRequest);
        return requestMapper.toItemRequestDto(new ItemRequestServiceDto(createdRequest, List.of()));
    }

    @GetMapping
    public List<ItemRequestDto> findAllByRequester(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) throws NotFoundException {
        List<ItemRequestServiceDto> foundRequests = requestService.findAllByRequester(userId);
        log.info("Найти запросы пользователя {}: {}", userId, foundRequests.size());
        return foundRequests.stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size
    ) throws ValidationException, NotFoundException {
        List<ItemRequestServiceDto> foundRequests = requestService.findAll(from, size, userId);
        log.info("Найти все запросы от {}, по {}", from, from + size);

        return foundRequests.stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());


    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId
    ) throws Throwable {
        ItemRequestServiceDto foundRequest = requestService.findById(requestId, userId);
        log.info("Найден запрос {}", requestId);
        return requestMapper.toItemRequestDto(foundRequest);
    }
}
