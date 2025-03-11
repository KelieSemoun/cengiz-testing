package com.openclassrooms.starterjwt.payload.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class SignupRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
    	ValidatorFactory factory = Validation.byDefaultProvider().configure().buildValidatorFactory();
    	validator = factory.getValidator();
    }

    @Test
    void testSignupRequestConstructorAndGetters() {
        // Arrange & Act
        SignupRequest request = new SignupRequest();
        request.setEmail("john.doe@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("securePassword");

        // Assert
        assertThat(request.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(request.getFirstName()).isEqualTo("John");
        assertThat(request.getLastName()).isEqualTo("Doe");
        assertThat(request.getPassword()).isEqualTo("securePassword");
    }

    @Test
    void testValidSignupRequest() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail("valid@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("ValidPassword123");

        // Act
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidEmail() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail("invalid-email"); // Invalid email
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("ValidPassword123");

        // Act
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void testInvalidFirstNameLength() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail("valid@example.com");
        request.setFirstName("J"); // Too short
        request.setLastName("Doe");
        request.setPassword("ValidPassword123");

        // Act
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
    }

    @Test
    void testInvalidPasswordLength() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail("valid@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("123"); // Too short

        // Act
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void testBlankFields() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail(""); // Blank
        request.setFirstName("");
        request.setLastName("");
        request.setPassword("");

        // Act
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(7);
    }

}
