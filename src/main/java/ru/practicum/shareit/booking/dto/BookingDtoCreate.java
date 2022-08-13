package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDtoCreate {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
