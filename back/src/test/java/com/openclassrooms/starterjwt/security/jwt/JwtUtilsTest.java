package com.openclassrooms.starterjwt.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private final String jwtSecret = "testSecretKey";
    private final int jwtExpirationMs = 3600000; // 1 hour
    private String validToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // ✅ Inject private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);

        // Mock Authentication
        UserDetailsImpl userPrincipal = new UserDetailsImpl(
                1L, "testUser", "John", "Doe", true, "securePassword"
        );
        
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        // Generate a valid JWT for testing
        validToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Test
    void testGenerateJwtToken() {
        // Act
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert
        assertThat(token).isNotNull();
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    void testGetUserNameFromJwtToken() {
        // Act
        String username = jwtUtils.getUserNameFromJwtToken(validToken);

        // Assert
        assertThat(username).isEqualTo("testUser");
    }

    @Test
    void testValidateJwtToken_ValidToken() {
        // Act
        boolean isValid = jwtUtils.validateJwtToken(validToken);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateJwtToken_InvalidSignature() {
        // Arrange - Create token with wrong secret key
        String invalidSignatureToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, "wrongSecretKey")
                .compact();

        // Act & Assert
        assertThat(jwtUtils.validateJwtToken(invalidSignatureToken)).isFalse();
    }

    @Test
    void testValidateJwtToken_ExpiredToken() {
        // Arrange - Create expired token
        String expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // Issued 1 hour ago
                .setExpiration(new Date(System.currentTimeMillis() - 1800000)) // Expired 30 mins ago
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        // Act & Assert
        assertThat(jwtUtils.validateJwtToken(expiredToken)).isFalse();
    }

    @Test
    void testValidateJwtToken_MalformedToken() {
        // Act & Assert
        assertThat(jwtUtils.validateJwtToken("malformedToken")).isFalse();
    }

    @Test
    void testValidateJwtToken_EmptyToken() {
        // Act & Assert
        assertThat(jwtUtils.validateJwtToken("")).isFalse();
    }
}
