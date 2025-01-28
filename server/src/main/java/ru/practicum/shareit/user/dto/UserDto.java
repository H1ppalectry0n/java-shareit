package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.user.validation.CreateUserValidation;
import ru.practicum.shareit.user.validation.UpdateUserValidation;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = {"id"})
public class UserDto {
    private Long id;

    @NotEmpty(message = "User name must not be empty", groups = CreateUserValidation.class)
    private String name;

    @NotNull(groups = CreateUserValidation.class, message = "must not be null")
    @Email(message = "invalid email", groups = {CreateUserValidation.class, UpdateUserValidation.class})
    private String email;
}
