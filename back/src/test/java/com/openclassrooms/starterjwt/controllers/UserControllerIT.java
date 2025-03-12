package com.openclassrooms.starterjwt.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private final Long userId = 1L;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAdmin(false);

        userDto = new UserDto(userId, "test@example.com", "Doe", "John", false, "hashedPassword", LocalDateTime.now(), LocalDateTime.now());

        when(userService.findById(userId)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testFindById_Success() throws Exception {
        mockMvc.perform(get("/api/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testFindById_NotFound() throws Exception {
        when(userService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/user/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testFindById_BadRequest() throws Exception {
        mockMvc.perform(get("/api/user/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testDeleteUser_Success() throws Exception {
        when(userService.findById(userId)).thenReturn(user);

        mockMvc.perform(delete("/api/user/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(userId);
    }

    @Test
    @WithMockUser(username = "otheruser@example.com", roles = {"USER"})
    void testDeleteUser_Unauthorized() throws Exception {
        when(userService.findById(userId)).thenReturn(user);

        mockMvc.perform(delete("/api/user/{id}", userId))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).delete(userId);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testDeleteUser_NotFound() throws Exception {
        when(userService.findById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/user/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(userService, never()).delete(999L);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testDeleteUser_BadRequest() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", "invalid"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).delete(any());
    }
}
