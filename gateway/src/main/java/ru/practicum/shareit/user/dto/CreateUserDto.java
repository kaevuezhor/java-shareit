package ru.practicum.shareit.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateUserDto {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
