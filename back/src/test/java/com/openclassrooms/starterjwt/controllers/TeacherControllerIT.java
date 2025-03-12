package com.openclassrooms.starterjwt.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

@SpringBootTest
@AutoConfigureMockMvc
class TeacherControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherMapper teacherMapper;

    private final Long teacherId = 1L;
    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    void setup() {
        teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        teacherDto = new TeacherDto(teacherId, "Doe", "John", LocalDateTime.now(), LocalDateTime.now());

        when(teacherService.findById(teacherId)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindAllTeachers_Success() throws Exception {
        List<Teacher> teachers = Collections.singletonList(teacher);
        List<TeacherDto> teacherDtos = Collections.singletonList(teacherDto);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(teacherId))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindById_Success() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", teacherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teacherId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindById_NotFound() throws Exception {
        when(teacherService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindById_BadRequest() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }
}
