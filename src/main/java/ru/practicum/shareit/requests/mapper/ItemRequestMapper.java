package ru.practicum.shareit.requests.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.requests.dto.ItemRequestServiceDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final ItemMapper itemMapper;

    public ItemRequestDto toItemRequestDto(ItemRequestServiceDto serviceDto) {
        return new ItemRequestDto(
                serviceDto.getItemRequest().getId(),
                serviceDto.getItemRequest().getDescription(),
                serviceDto.getItemRequest().getCreated(),
                serviceDto.getItems().stream().map(itemMapper::toItemDto).collect(Collectors.toList())
        );
    }
}
