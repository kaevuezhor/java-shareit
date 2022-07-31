package ru.practicum.shareit.requests.mapper;

import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated()
        );
    }
}
