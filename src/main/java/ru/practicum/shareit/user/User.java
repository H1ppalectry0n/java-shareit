package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.validation.CreateUserValidation;
import ru.practicum.shareit.user.validation.UpdateUserValidation;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;

    @NotEmpty(message = "User name must not be empty", groups = CreateUserValidation.class)
    private String name;

    @NotNull(groups = CreateUserValidation.class)
    @Email(message = "invalid email", groups = {CreateUserValidation.class, UpdateUserValidation.class})
    private String email;
}
