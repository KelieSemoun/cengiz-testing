package com.openclassrooms.starterjwt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session testSession;
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

        testSession = Session.builder()
            .id(1L)
            .name("Math Class")
            .description("A session about advanced mathematics.")
            .users(new ArrayList<>(List.of(testUser)))
            .build();
    }

    @Test
    void testCreate() {
        // Arrange
        when(sessionRepository.save(testSession)).thenReturn(testSession);

        // Act
        Session savedSession = sessionService.create(testSession);

        // Assert
        assertThat(savedSession).isNotNull().isEqualTo(testSession);
        verify(sessionRepository, times(1)).save(testSession);
    }

    @Test
    void testDelete() {
        // Act
        sessionService.delete(1L);

        // Assert
        verify(sessionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindAll() {
        // Arrange
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(testSession));

        // Act
        List<Session> sessions = sessionService.findAll();

        // Assert
        assertThat(sessions)
            .isNotEmpty()
            .hasSize(1)
            .containsExactly(testSession);

        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void testGetById_WhenSessionExists() {
        // Arrange
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        // Act
        Session foundSession = sessionService.getById(1L);

        // Assert
        assertThat(foundSession).isNotNull().isEqualTo(testSession);
        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_WhenSessionDoesNotExist() {
        // Arrange
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Session foundSession = sessionService.getById(1L);

        // Assert
        assertThat(foundSession).isNull();
        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdate() {
        // Arrange
        when(sessionRepository.save(testSession)).thenReturn(testSession);

        // Act
        Session updatedSession = sessionService.update(1L, testSession);

        // Assert
        assertThat(updatedSession).isNotNull().isEqualTo(testSession);
        assertThat(updatedSession.getId()).isEqualTo(1L);
        verify(sessionRepository, times(1)).save(testSession);
    }

    @Test
    void testParticipate_WhenUserNotAlreadyInSession() {
        // Arrange
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUser));

        // Act
        sessionService.participate(1L, 2L);

        // Assert
        verify(sessionRepository, times(1)).save(testSession);
    }

    @Test
    void testParticipate_WhenUserAlreadyInSession() {
        // Arrange
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> sessionService.participate(1L, 1L))
            .isInstanceOf(BadRequestException.class);

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void testUnparticipate_WhenUserExists() {
        // Arrange
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        // Act
        sessionService.noLongerParticipate(1L, 1L);

        // Assert
        verify(sessionRepository, times(1)).save(testSession);
    }
}
