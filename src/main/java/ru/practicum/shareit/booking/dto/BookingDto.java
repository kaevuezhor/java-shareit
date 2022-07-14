package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingDto {
    private int id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private BookingStatus status;
}
