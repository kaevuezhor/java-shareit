package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDate;

/**
 * // TODO .
 */
@Data
public class BookingDto {
    private int id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private BookingStatus status;
}
