package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class BookingDtoCreate {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
