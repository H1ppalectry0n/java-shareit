package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemCreateDtoTest {

    private JacksonTester<ItemCreateDto> json;
    private Validator validator;

    @BeforeEach
    void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerialization() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto(1L, "Item Name", "Item Description", true, 2L);

        JsonContent<ItemCreateDto> result = json.write(itemCreateDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item Name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Item Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }

    @Test
    void testDeserialization() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true,\"requestId\":2}";

        ObjectContent<ItemCreateDto> result = json.parse(jsonContent);
        ItemCreateDto itemCreateDto = result.getObject();

        assertThat(itemCreateDto.getId()).isEqualTo(1L);
        assertThat(itemCreateDto.getName()).isEqualTo("Item Name");
        assertThat(itemCreateDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemCreateDto.getAvailable()).isTrue();
        assertThat(itemCreateDto.getRequestId()).isEqualTo(2L);
    }

    @Test
    void testValidationConstraints() {
        ItemCreateDto invalidDto = new ItemCreateDto(null, "", " ", null, null);

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(invalidDto);

        assertThat(violations).hasSize(3);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name")
                && v.getMessage().equals("Name must not be blank"));

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description")
                && v.getMessage().equals("description must not be blank"));

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("available")
                && v.getMessage().equals("Availability must be specified"));
    }
}
