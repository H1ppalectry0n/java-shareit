package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.user.validation.CreateUserValidation;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(ObjectMapper.class)
public class UserDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUserDto() throws Exception {
        String json = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, CreateUserValidation.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidUserDtoName() throws Exception {
        String json = "{\"id\":1,\"name\":\"\",\"email\":\"john.doe@example.com\"}";
        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, CreateUserValidation.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("User name must not be empty");
    }

    @Test
    void testInvalidUserDtoEmail() throws Exception {
        String json = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"invalid-email\"}";
        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, CreateUserValidation.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("invalid email");
    }

    @Test
    void testMissingEmailOnCreate() throws Exception {
        String json = "{\"id\":1,\"name\":\"John Doe\"}";
        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, CreateUserValidation.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("must not be null");
    }
}
