package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

@Component
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new UserDtoShort(booking.getBooker().getId()),
                new ItemDtoShort(booking.getItem().getId(), booking.getItem().getName())
        );
    }
}
