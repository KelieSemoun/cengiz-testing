package com.openclassrooms.starterjwt.security.jwt;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
class AuthTokenFilterIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtils jwtUtils;

    private final String validToken = "valid.jwt.token";
    private final String username = "testuser";

    @BeforeEach
    void setupMocks() {
        // Mock JWTUtils behavior
        when(jwtUtils.validateJwtToken(validToken)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(validToken)).thenReturn(username);

        // Mock UserDetailsService
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    }

    @Test
    void testValidJwtAuthentication() throws Exception {
    	mockMvc.perform(get("/api/teacher") 
    	        .header("Authorization", "Bearer " + validToken))
    	        .andExpect(status().isOk()); 

    }

    @Test
    void testInvalidJwtAuthentication() throws Exception {
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isUnauthorized());
    }
}
