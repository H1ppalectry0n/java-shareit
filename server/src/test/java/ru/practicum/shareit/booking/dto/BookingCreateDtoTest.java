package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoTest {

    private JacksonTester<BookingCreateDto> json;
    private Validator validator;

    @BeforeEach
    void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register JavaTimeModule for LocalDateTime support
        JacksonTester.initFields(this, objectMapper);

        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.afterPropertiesSet();
        validator = factoryBean.getValidator();
    }

    @Test
    void testSerialization() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, LocalDateTime.of(2023, 12, 1, 12, 0), LocalDateTime.of(2023, 12, 2, 12, 0));
        JsonContent<BookingCreateDto> result = json.write(bookingCreateDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathArrayValue("$.start").containsExactly(2023, 12, 1, 12, 0);
        assertThat(result).extractingJsonPathArrayValue("$.end").containsExactly(2023, 12, 2, 12, 0);
    }

    @Test
    void testDeserialization() throws Exception {
        String jsonContent = "{" +
                "\"itemId\": 1," +
                "\"start\": \"2023-12-01T12:00:00\"," +
                "\"end\": \"2023-12-02T12:00:00\"}";

        ObjectContent<BookingCreateDto> result = json.parse(jsonContent);
        BookingCreateDto bookingCreateDto = result.getObject();

        assertThat(bookingCreateDto.getItemId()).isEqualTo(1L);
        assertThat(bookingCreateDto.getStart()).isEqualTo(LocalDateTime.of(2023, 12, 1, 12, 0));
        assertThat(bookingCreateDto.getEnd()).isEqualTo(LocalDateTime.of(2023, 12, 2, 12, 0));
    }

    @Test
    void testValidationSuccess() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, LocalDateTime.of(2023, 12, 1, 12, 0), LocalDateTime.of(2023, 12, 2, 12, 0));

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(bookingCreateDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFailure() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(null, null, null);

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(bookingCreateDto);

        assertThat(violations).hasSize(3);
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Item ID cannot be null"));
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Start date cannot be null"));
        assertThat(violations).anyMatch(v -> v.getMessage().equals("End date cannot be null"));
    }
}