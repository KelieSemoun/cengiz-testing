package com.openclassrooms.starterjwt.payload.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testLoginRequestConstructorAndGetters() {
        // Arrange & Act
        LoginRequest request = new LoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("securePassword");

        // Assert
        assertThat(request.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(request.getPassword()).isEqualTo("securePassword");
    }

    @Test
    void testValidLoginRequest() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("valid@example.com");
        request.setPassword("ValidPassword123");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidBlankFields() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail(""); // Blank email
        request.setPassword(""); // Blank password

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(2); // ✅ Expect 2 violations (email & password)
    }
}
