package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.dto.ItemRequestServiceDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface RequestService {
    ItemRequest createRequest(ItemRequest itemRequest, long userId) throws NotFoundException;

    List<ItemRequestServiceDto> findAllByRequester(long userId) throws NotFoundException;

    List<ItemRequestServiceDto> findAll(int from, int size, long userId) throws NotFoundException;

    ItemRequestServiceDto findById(long id, long userId) throws Throwable;
}
