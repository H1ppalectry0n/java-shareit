package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookItemRequestDtoTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    private final Validator validator;

    public BookItemRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testSerialization() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        String content = json.write(dto).getJson();

        assertThat(content).contains("itemId", "start", "end");
    }

    @Test
    void testDeserialization() throws Exception {
        String content = "{" +
                "\"itemId\":1," +
                "\"start\":\"2025-01-28T12:00:00\"," +
                "\"end\":\"2025-01-29T12:00:00\"}";

        BookItemRequestDto dto = json.parse(content).getObject();

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 1, 28, 12, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 1, 29, 12, 0));
    }

    @Test
    void testValidDtoValidation() {
        BookItemRequestDto validDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(validDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidDtoWithPastStartTime() {
        BookItemRequestDto invalidDto = new BookItemRequestDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2));
        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(invalidDto);
        assertThat(violations).hasSize(1);
    }

    @Test
    void testInvalidDtoWithNullFields() {
        BookItemRequestDto invalidDto = new BookItemRequestDto(null, null, null);
        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(invalidDto);
        assertThat(violations).hasSize(3);
    }
}
