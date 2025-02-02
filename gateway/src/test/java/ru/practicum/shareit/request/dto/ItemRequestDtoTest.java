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

import java.io.IOException;
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
                "Test Description"
        );
    }

    @Test
    void testItemRequestDto() throws IOException {
        var jsonContent = json.write(itemRequestDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Test Description");
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