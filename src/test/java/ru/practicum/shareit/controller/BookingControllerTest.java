package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private Item testItem = new Item(1L, "item", "desc", true, 1L, null);

    private LocalDateTime testStartTime = LocalDateTime.of(2022,1,1,1,1,1);

    private LocalDateTime testStartEnd = LocalDateTime.of(2022,1,2,1,1,1);

    private User testUser1 = new User(1L, "name", "e@m.l");

    private User testUser2 = new User(2L, "na2me", "2e@m.l");

    private Booking testBookingWaiting = new Booking(
            1L,
            testStartTime,
            testStartEnd,
            testItem,
            testUser2,
            BookingStatus.WAITING
    );

    private Booking testBookingApproved = new Booking(
            1L,
            testStartTime,
            testStartEnd,
            testItem,
            testUser2,
            BookingStatus.APPROVED
    );

    @Test
    void testCreateBooking() throws Exception {
        BookingDtoCreate requestBody = new BookingDtoCreate(
            1L,
            testStartEnd,
            testStartEnd
        );
        BookingDto expectedDto = new BookingDto(
                testBookingWaiting.getId(),
                testBookingWaiting.getStart(),
                testBookingWaiting.getEnd(),
                testBookingWaiting.getStatus(),
                new UserDtoShort(testBookingWaiting.getBooker().getId()),
                new ItemDtoShort(testBookingWaiting.getItem().getId(), testBookingWaiting.getItem().getName())
        );

        when(bookingService.createBooking(requestBody,testUser2.getId())).thenReturn(testBookingWaiting);
        when(bookingMapper.toBookingDto(testBookingWaiting)).thenReturn(expectedDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBody))
                        .header("X-Sharer-User-Id", testUser2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.start").value(expectedDto.getStart()))
                .andExpect(jsonPath("$.end").value(expectedDto.getEnd()))
                .andExpect(jsonPath("$.status").value(expectedDto.getStatus()));
    }

    @Test
    void testApproveBooking() throws Exception {
        BookingDto expectedDto = new BookingDto(
                testBookingApproved.getId(),
                testBookingApproved.getStart(),
                testBookingApproved.getEnd(),
                testBookingApproved.getStatus(),
                new UserDtoShort(testBookingApproved.getBooker().getId()),
                new ItemDtoShort(testBookingApproved.getItem().getId(), testBookingWaiting.getItem().getName())
        );

        when(bookingService.approveBooking(testBookingWaiting.getId(), true, testUser1.getId()))
                .thenReturn(testBookingApproved);
        when(bookingMapper.toBookingDto(testBookingApproved))
                .thenReturn(expectedDto);

        mockMvc.perform(patch("/bookings/{id}", testBookingWaiting.getId())
                        .param("approved", String.valueOf(true))
                        .header("X-Sharer-User-Id", testUser1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.start").value(expectedDto.getStart()))
                .andExpect(jsonPath("$.end").value(expectedDto.getEnd()))
                .andExpect(jsonPath("$.status").value(expectedDto.getStatus()));
    }
}
