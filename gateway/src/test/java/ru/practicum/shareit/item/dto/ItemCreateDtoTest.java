package ru.practicum.shareit.item.dto;

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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemCreateDtoTest {
    private final JacksonTester<ItemCreateDto> json;

    private Validator validator;

    private ItemCreateDto itemDto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        itemDto = new ItemCreateDto(1L, "Test Item", "A description for testing", true, 123L);
    }

    @Test
    void testSerialization() throws Exception {
        var jsonContent = json.write(itemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Test Item");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("A description for testing");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(123);
    }

    @Test
    void testDeserialization() throws Exception {
        String object = "{\"id\": 1, \"name\": \"Test Item\", \"description\": \"A description for testing\", \"available\": true, \"requestId\": 123}";

        var parsed = json.parse(object);

        assertThat(parsed.getObject().getId()).isEqualTo(1L);
        assertThat(parsed.getObject().getName()).isEqualTo("Test Item");
        assertThat(parsed.getObject().getDescription()).isEqualTo("A description for testing");
        assertThat(parsed.getObject().getAvailable()).isTrue();
        assertThat(parsed.getObject().getRequestId()).isEqualTo(123L);
    }

    @Test
    void testDeserializationWithValidationAndRequestId() {
        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemDto);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    void testDeserializationWithValidationInvalidData() {
        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);
        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemDto);

        assertThat(violations.size()).isEqualTo(3);
    }

    @Test
    void testDeserializationWithValidationWithOutRequestId() {
        itemDto.setRequestId(null);
        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemDto);

        assertThat(violations.size()).isEqualTo(0);
    }

}