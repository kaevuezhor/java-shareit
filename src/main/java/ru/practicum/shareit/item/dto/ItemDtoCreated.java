package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItemDtoCreated {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
