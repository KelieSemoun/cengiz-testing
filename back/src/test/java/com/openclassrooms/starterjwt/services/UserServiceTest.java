package com.openclassrooms.starterjwt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
            .id(1L)
            .email("john.doe@example.com")
            .firstName("John")
            .lastName("Doe")
            .password("securepassword")
            .admin(false)
            .build();
    }

    @Test
    void testFindById_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.findById(1L);

        // Assert
        assertThat(foundUser)
            .isNotNull()
            .extracting(User::getId, User::getEmail, User::getFirstName, User::getLastName, User::isAdmin)
            .containsExactly(1L, "john.doe@example.com", "John", "Doe", false);

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        User foundUser = userService.findById(1L);

        // Assert
        assertThat(foundUser).isNull();

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testDelete() {
        // Act
        userService.delete(1L);

        // Assert (verify that deleteById was called)
        verify(userRepository, times(1)).deleteById(1L);
    }
}
