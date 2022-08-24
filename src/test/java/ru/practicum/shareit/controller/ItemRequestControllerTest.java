package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.controller.ItemRequestController;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestServiceDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestService requestService;

    @MockBean
    private ItemRequestMapper itemRequestMapper;

    @Autowired
    private MockMvc mockMvc;

    private User testUser1 = new User(1L, "name", "e@m.l");

    private User testUser2 = new User(2L, "na2me", "2e@m.l");

    private Item testItem = new Item(1L, "item", "desc", true, 1L, null);

    private ItemRequest testRequest = new ItemRequest(
                1L,
                "need item",
                testUser2,
                LocalDateTime.of(2022,1,1,1,1,1)
            );

    @Test
    void testCreateRequest() throws Exception {
        ItemRequest requestBody = new ItemRequest(
                "need item"
        );
        ItemRequestDto expectedDto = new ItemRequestDto(
                testRequest.getId(),
                testRequest.getDescription(),
                testRequest.getCreated(),
                List.of()
        );

        when(requestService.createRequest(requestBody, testUser2.getId()))
                .thenReturn(testRequest);
        when(itemRequestMapper.toItemRequestDto(new ItemRequestServiceDto(testRequest, List.of())))
                .thenReturn(expectedDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", testUser2.getId())
                        .content(mapper.writeValueAsString(requestBody))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.description").value(expectedDto.getDescription()))
                .andExpect(jsonPath("$.created").value(expectedDto.getCreated()));
    }

    @Test
    void testFindAllByRequester() throws Exception {
        ItemRequestServiceDto requestDto = new ItemRequestServiceDto(testRequest, List.of());

        when(requestService.findAllByRequester(testUser2.getId()))
                .thenReturn(List.of(requestDto));
        when(itemRequestMapper.toItemRequestDto(requestDto))
                .thenReturn(new ItemRequestDto(
                        testRequest.getId(),
                        testRequest.getDescription(),
                        testRequest.getCreated(),
                        List.of()
                ));

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", testUser2.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAll() throws Exception {
        ItemRequestServiceDto requestDto = new ItemRequestServiceDto(testRequest, List.of());

        when(requestService.findAll(0, 10, testUser2.getId()))
                .thenReturn(List.of(requestDto));
        when(itemRequestMapper.toItemRequestDto(requestDto))
                .thenReturn(new ItemRequestDto(
                        testRequest.getId(),
                        testRequest.getDescription(),
                        testRequest.getCreated(),
                        List.of()
                ));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", testUser2.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testFindById() throws Throwable {
        ItemRequestServiceDto foundRequest = new ItemRequestServiceDto(testRequest, List.of());
        ItemRequestDto expectedDto = new ItemRequestDto(
                testRequest.getId(),
                testRequest.getDescription(),
                testRequest.getCreated(),
                List.of()
        );

        when(requestService.findById(testRequest.getId(), testUser2.getId()))
                .thenReturn(foundRequest);
        when(itemRequestMapper.toItemRequestDto(foundRequest)).thenReturn(expectedDto);

        mockMvc.perform(get("/requests/{requestId}", testRequest.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", testUser2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.description").value(expectedDto.getDescription()))
                .andExpect(jsonPath("$.created").value(expectedDto.getCreated()));
    }
}
