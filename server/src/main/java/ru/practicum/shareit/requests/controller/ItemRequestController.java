package ru.practicum.shareit.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
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
    ) throws NotFoundException {
        ItemRequest createdRequest = requestService.createRequest(request, userId);
        log.info("Created request {} by user id={}", createdRequest, userId);
        return requestMapper.toItemRequestDto(new ItemRequestServiceDto(createdRequest, List.of()));
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) throws NotFoundException {
        List<ItemRequestServiceDto> foundRequests = requestService.findAllByRequester(userId);
        log.info("Get requests user id={}, found {}", userId, foundRequests.size());
        return foundRequests.stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size
    ) throws NotFoundException {
        List<ItemRequestServiceDto> foundRequests = requestService.findAll(from, size, userId);
        log.info("Get requests from={}, size={} by user id={}, found {}", from, size, userId, foundRequests.size());
        return foundRequests.stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());


    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(
            @PathVariable long requestId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) throws Throwable {
        ItemRequestServiceDto foundRequest = requestService.findById(requestId, userId);
        log.info("Get request id={} by user id={}", requestId, userId);
        return requestMapper.toItemRequestDto(foundRequest);
    }
}
