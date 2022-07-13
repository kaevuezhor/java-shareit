package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDate;

/**
 * // TODO .
 */
@Data
public class Booking {
    private int id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private BookingStatus status;
}
