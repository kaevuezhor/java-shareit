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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserMapper userMapper;
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private User testUser = new User(1L, "name", "e@m.l");

    private UserDto testUserDto = new UserDto(
            testUser.getId(),
            testUser.getName(),
            testUser.getEmail()
    );

    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(any(User.class)))
                .thenReturn(testUser);
        when(userMapper.toUserDto(testUser))
                .thenReturn(testUserDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(new User(
                                null,
                                "name",
                                "e@m.l"
                        )))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    void testGetUser() throws Exception {
        when(userService.getUser(any(Long.class)))
                .thenReturn(testUser);
        when(userMapper.toUserDto(testUser))
                .thenReturn(testUserDto);

        mockMvc.perform(get("/users/{id}", testUser.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(testUser));
        when(userMapper.toUserDto(testUser))
                .thenReturn(testUserDto);

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(testUser))));
    }

    @Test
    void testUpdateUser() throws Exception {
        User patch = new User(null, "amen", null);
        User expectedUser = testUser;
        expectedUser.setName(patch.getName());
        UserDto expectedUserDto = new UserDto(
                expectedUser.getId(),
                expectedUser.getName(),
                expectedUser.getEmail()
        );

        when(userService.updateUser(anyLong(), any(User.class)))
                .thenReturn(expectedUser);
        when(userMapper.toUserDto(any(User.class)))
                .thenReturn(expectedUserDto);

        mockMvc.perform(patch("/users/{id}", testUser.getId())
                        .content(mapper.writeValueAsString(patch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUserDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedUserDto.getName()))
                .andExpect(jsonPath("$.email").value(expectedUserDto.getEmail()));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", testUser.getId()))
                .andExpect(status().isOk());
        when(userService.getUser(testUser.getId()))
                .thenThrow(new NotFoundException("Пользователь 1 не найден"));
        mockMvc.perform(get("/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


}
