package com.openclassrooms.starterjwt.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;
    
    private ObjectMapper objectMapper;
    
    private final Long sessionId = 1L;
    private final Long userId = 100L;
    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setup() {
    	objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
         
        session = new Session();
        session.setId(sessionId);
        session.setName("Yoga Session");

        sessionDto = new SessionDto();
        sessionDto.setId(sessionId);
        sessionDto.setName("Yoga Session");

        when(sessionService.getById(sessionId)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testFindAllSessions_Success() throws Exception {
        List<Session> sessions = Collections.singletonList(session);
        List<SessionDto> sessionDtos = Collections.singletonList(sessionDto);

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(sessionId));
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testFindById_Success() throws Exception {
        mockMvc.perform(get("/api/session/{id}", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId));
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testFindById_NotFound() throws Exception {
        when(sessionService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/session/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testFindById_BadRequest() throws Exception {
        mockMvc.perform(get("/api/session/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testCreateSession_Success() throws Exception {
        SessionDto sessionDto = new SessionDto(
                null,
                "Yoga Session",
                new Date(),
                10L,
                "A relaxing yoga class",
                List.of(1L, 2L, 3L), 
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(sessionService.create(any(Session.class))).thenReturn(session);
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto);

        mockMvc.perform(post("/api/session")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Session"));
    }


    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
 
    void testUpdateSession_Success() throws Exception {
        SessionDto sessionDto = new SessionDto(
                sessionId,
                "Yoga Session",
                new Date(),
                10L,
                "A relaxing yoga class",
                List.of(1L, 2L, 3L),
                LocalDateTime.now(),
                LocalDateTime.now() 
        );

        when(sessionService.update(eq(sessionId), any(Session.class))).thenReturn(session);
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto);

        mockMvc.perform(put("/api/session/{id}", sessionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId));
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateSession_BadRequest() throws Exception {
        SessionDto sessionDto = new SessionDto(sessionId, "Yoga Session", new Date(), 1L, "Description", List.of(), LocalDateTime.now(), LocalDateTime.now());

        mockMvc.perform(put("/api/session/{id}", "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testDeleteSession_Success() throws Exception {
        when(sessionService.getById(sessionId)).thenReturn(session);
        doNothing().when(sessionService).delete(sessionId);

        mockMvc.perform(delete("/api/session/{id}", sessionId))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testDeleteSession_NotFound() throws Exception {
        when(sessionService.getById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/session/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testDeleteSession_BadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testParticipate_Success() throws Exception {
        doNothing().when(sessionService).participate(sessionId, userId);

        mockMvc.perform(post("/api/session/{id}/participate/{userId}", sessionId, userId))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testParticipate_BadRequest() throws Exception {
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", "invalid", userId))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testNoLongerParticipate_Success() throws Exception {
        doNothing().when(sessionService).noLongerParticipate(sessionId, userId);

        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", sessionId, userId))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testNoLongerParticipate_BadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", "invalid", userId))
                .andExpect(status().isBadRequest());
    }
}
