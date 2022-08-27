package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.dto.ItemDtoUserView;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemMapper itemMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private User testUser = new User(1L, "name", "e@m.l");

    private Item testItem = new Item(1L, "item", "desc", true, 1L, null);

    private ItemDto expectedItemDto = new ItemDto(testItem.getId(), testItem.getName(), testItem.getDescription(), testItem.getAvailable(), null);

    @Test
    void testCreateItem() throws Exception {
        ItemDtoCreated requestBody = new ItemDtoCreated("item", "desc", true, null);

        when(itemService.createItem(any(ItemDtoCreated.class), anyLong()))
                .thenReturn(testItem);
        when(itemMapper.toItemDto(any(Item.class)))
                .thenReturn(expectedItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .content(mapper.writeValueAsString(requestBody))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedItemDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(expectedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(expectedItemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").isEmpty());
    }

    @Test
    void testGetItem() throws Exception {
        ItemDtoService itemDtoService = new ItemDtoService(testItem, List.of(), List.of());
        ItemDtoUserView itemDtoUserView = new ItemDtoUserView(
                testItem.getId(),
                testItem.getName(),
                testItem.getDescription(),
                testItem.getAvailable(),
                null,
                null,
                List.of()
        );

        when(itemService.getItem(testUser.getId(), testUser.getId()))
                .thenReturn(itemDtoService);
        when(itemMapper.toItemDtoUserView(itemDtoService))
                .thenReturn(itemDtoUserView);

        mockMvc.perform(get("/items/{id}", testItem.getId())
                        .header("X-Sharer-User-Id", testUser.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoUserView.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoUserView.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoUserView.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoUserView.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").isEmpty())
                .andExpect(jsonPath("$.nextBooking").isEmpty())
                .andExpect(jsonPath("$.comments").isEmpty());
    }

    @Test
    void testSearchItems() throws Exception {
        String searchText = "item";

        when(itemService.searchItems(searchText, 0, 10))
                .thenReturn(List.of(testItem));
        when(itemMapper.toItemDto(testItem))
                .thenReturn(expectedItemDto);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(expectedItemDto))));
    }

    @Test
    void testGetAllItems() throws Exception {
        ItemDtoService itemDtoService = new ItemDtoService(testItem, List.of(), List.of());
        ItemDtoUserView itemDtoUserView = new ItemDtoUserView(
                testItem.getId(),
                testItem.getName(),
                testItem.getDescription(),
                testItem.getAvailable(),
                null,
                null,
                List.of()
        );

        when(itemService.getAllUserItems(testUser.getId(), 0, 10)).thenReturn(List.of(itemDtoService));
        when(itemMapper.toItemDtoUserView(itemDtoService)).thenReturn(itemDtoUserView);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoUserView))));
    }

    @Test
    void testUpdateItem() throws Exception {
        Item patch = new Item(null, "itemitem", null, null, null, null);
        Item updatedItem = testItem;
        updatedItem.setName(patch.getName());

        when(itemService.updateItem(testItem.getId(), patch, testUser.getId()))
                .thenReturn(updatedItem);
        when(itemMapper.toItemDto(updatedItem)).thenReturn(new ItemDto(
           updatedItem.getId(),
                updatedItem.getName(),
                updatedItem.getDescription(),
                updatedItem.getAvailable(),
                null
        ));

        mockMvc.perform(patch("/items/{id}", testItem.getId())
                        .header("X-Sharer-User-Id", testUser.getId())
                        .content(mapper.writeValueAsString(patch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedItemDto.getId()))
                .andExpect(jsonPath("$.name").value(updatedItem.getName()))
                .andExpect(jsonPath("$.description").value(expectedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(expectedItemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").isEmpty());
    }

    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/{id}", testItem.getId())
                        .header("X-Sharer-User-Id", testUser.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        when(itemService.getItem(testItem.getId(), testUser.getId()))
                .thenThrow(new NotFoundException("Предмет 1 не найден"));
        mockMvc.perform(get("/items/{id}", testUser.getId())
                        .header("X-Sharer-User-Id", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
