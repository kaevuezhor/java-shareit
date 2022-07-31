package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.dto.ItemDtoUserView;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemDtoUserView toItemDtoUserView(ItemDtoService itemDtoService) {
        Item item = itemDtoService.getItem();
        List<Booking> bookings = itemDtoService.getBookings();
        List<Comment> comments = itemDtoService.getComments();
        Optional<Booking> lastBooking = getLastBooking(bookings, item);
        Optional<Booking> nextBooking = getNextBooking(bookings, item);
        BookingDtoItem lastBookingDto = null;
        BookingDtoItem nextBookingDto = null;
        if (lastBooking.isPresent()) {
            lastBookingDto = new BookingDtoItem();
            lastBookingDto.setId(lastBooking.get().getId());
            lastBookingDto.setBookerId(lastBooking.get().getBooker().getId());
        }
        if (nextBooking.isPresent()) {
            nextBookingDto = new BookingDtoItem();
            nextBookingDto.setId(nextBooking.get().getId());
            nextBookingDto.setBookerId(nextBooking.get().getBooker().getId());
        }
        List<CommentDto> commentsDto = comments.stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList());
        return new ItemDtoUserView(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingDto,
                nextBookingDto,
                commentsDto
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
        return commentDto;
    }

    private static Optional<Booking> getLastBooking(List<Booking> bookings, Item item) {
        return bookings.stream()
                .filter(b -> b.getEnd().isBefore(ChronoLocalDateTime.from(LocalDateTime.now()))).findFirst();
    }

    private static Optional<Booking> getNextBooking(List<Booking> bookings, Item item) {
        return bookings.stream()
                .filter(b -> b.getEnd().isAfter(ChronoLocalDateTime.from(LocalDateTime.now()))).findFirst();
    }
}
