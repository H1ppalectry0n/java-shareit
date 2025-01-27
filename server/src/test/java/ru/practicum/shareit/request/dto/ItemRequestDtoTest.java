package ru.practicum.shareit.request.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestDtoTest {
    private final JacksonTester<ItemRequestDto> json;

    private Validator validator;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        itemRequestDto = new ItemRequestDto(
                1L,
                "Test Description",
                new UserDto(5L, "Test User", "Test email"),
                LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                new ArrayList<>()
        );
    }

    @Test
    void testItemRequestDto() throws IOException {
        var jsonContent = json.write(itemRequestDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Test Description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo("2025-01-01T10:00:00");
    }

    @Test
    void testDeserialization() {
        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    void testDeserializationInvalidData() {
        itemRequestDto.setDescription("");
        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);

        assertThat(violations.size()).isEqualTo(1);
    }

}