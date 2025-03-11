package com.openclassrooms.starterjwt.payload.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtResponseTest {

    @Test
    void testJwtResponseConstructorAndGetters() {
        // Arrange & Act
        JwtResponse jwtResponse = new JwtResponse("token123", 1L, "johndoe", "John", "Doe", true);

        // Assert
        assertThat(jwtResponse).isNotNull();
        assertThat(jwtResponse.getToken()).isEqualTo("token123");
        assertThat(jwtResponse.getType()).isEqualTo("Bearer");
        assertThat(jwtResponse.getId()).isEqualTo(1L);
        assertThat(jwtResponse.getUsername()).isEqualTo("johndoe");
        assertThat(jwtResponse.getFirstName()).isEqualTo("John");
        assertThat(jwtResponse.getLastName()).isEqualTo("Doe");
        assertThat(jwtResponse.getAdmin()).isTrue();
    }

    @Test
    void testJwtResponseSetters() {
        // Arrange
        JwtResponse jwtResponse = new JwtResponse("token123", 1L, "johndoe", "John", "Doe", true);

        // Act
        jwtResponse.setToken("newToken456");
        jwtResponse.setUsername("janedoe");
        jwtResponse.setFirstName("Jane");
        jwtResponse.setLastName("Smith");
        jwtResponse.setAdmin(false);

        // Assert
        assertThat(jwtResponse.getToken()).isEqualTo("newToken456");
        assertThat(jwtResponse.getUsername()).isEqualTo("janedoe");
        assertThat(jwtResponse.getFirstName()).isEqualTo("Jane");
        assertThat(jwtResponse.getLastName()).isEqualTo("Smith");
        assertThat(jwtResponse.getAdmin()).isFalse();
    }
}
