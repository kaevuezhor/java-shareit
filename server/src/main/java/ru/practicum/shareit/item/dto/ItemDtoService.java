package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoService {
    private Item item;
    private List<Booking> bookings;
    private List<Comment> comments;
}
