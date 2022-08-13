package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface RequestService {
    ItemRequest createRequest(ItemRequest itemRequest, long userId) throws NotFoundException, ValidationException;

    List<ItemRequest> findAllByRequester(long userId) throws NotFoundException;

    List<ItemRequest> findAll(int from, int size);

    ItemRequest findById(long id);
}
