package com.openclassrooms.starterjwt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        // Arrange
    	Teacher teacher1 = Teacher.builder()
    		    .id(1L)
    		    .firstName("John")
    		    .lastName("Doe")
    		    .build();
    	Teacher teacher2 = Teacher.builder()
    		    .id(2L)
    		    .firstName("Jane")
    		    .lastName("Doe")
    		    .build();
        when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher1, teacher2));

        // Act
        List<Teacher> teachers = teacherService.findAll();

        // Assert using AssertJ
        assertThat(teachers)
            .isNotEmpty()
            .hasSize(2)
            .extracting(Teacher::getFirstName, Teacher::getLastName)
            .containsExactly(
            	tuple("John", "Doe"),
            	tuple("Jane",  "Doe")
            );

        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void testFindById_WhenTeacherExists() {
        // Arrange
    	Teacher teacher = Teacher.builder()
    		    .id(1L)
    		    .firstName("John")
    		    .lastName("Doe")
    		    .build();
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // Act
        Teacher foundTeacher = teacherService.findById(1L);

        // Assert using AssertJ
        assertThat(foundTeacher)
            .isNotNull()
            .extracting(Teacher::getId, Teacher::getFirstName, Teacher::getLastName)
            .containsExactly(1L, "John", "Doe");

        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_WhenTeacherDoesNotExist() {
        // Arrange
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Teacher foundTeacher = teacherService.findById(1L);

        // Assert using AssertJ
        assertThat(foundTeacher).isNull();

        verify(teacherRepository, times(1)).findById(1L);
    }
}
