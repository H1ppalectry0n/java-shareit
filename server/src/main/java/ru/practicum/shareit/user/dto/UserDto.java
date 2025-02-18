package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = {"id"})
public class UserDto {
    private Long id;

    private String name;

    private String email;
}
