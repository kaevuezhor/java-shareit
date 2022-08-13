package ru.practicum.shareit.requests.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }
}
