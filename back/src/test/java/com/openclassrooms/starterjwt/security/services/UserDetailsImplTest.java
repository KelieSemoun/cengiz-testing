package com.openclassrooms.starterjwt.security.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class UserDetailsImplTest {

    @Test
    void testUserDetailsImplCreation() {
        // Arrange & Act
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("securepassword")
                .build();

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("john.doe");
        assertThat(userDetails.getFirstName()).isEqualTo("John");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getPassword()).isEqualTo("securepassword");
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getAdmin()).isFalse();
    }

    @Test
    void testSecurityMethodsReturnTrue() {
        // Arrange
        UserDetailsImpl userDetails = UserDetailsImpl.builder().build();

        // Act & Assert
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void testGetAuthoritiesReturnsEmptyCollection() {
        // Arrange
        UserDetailsImpl userDetails = UserDetailsImpl.builder().build();

        // Act
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Assert
        assertThat(authorities).isInstanceOf(HashSet.class);
        assertThat(authorities).isEmpty();
    }

    @Test
    void testEqualsMethod() {
        // Arrange
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user3 = UserDetailsImpl.builder().id(2L).build();

        // Act & Assert
        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1).isNotEqualTo(null);
        assertThat(user1).isNotEqualTo(new Object());
    }
}
