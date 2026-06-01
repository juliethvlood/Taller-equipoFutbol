package com.equipofutbol.equipofutbol_adso.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "WCrD7ZwsNH2yZx4d29X3522BEAYTjm7BiNJxi9xksjU=");
        ReflectionTestUtils.setField(jwtService, "tokenExpiration", 600000L);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken("1", "ADMINISTRATOR", "testuser");

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        String token = jwtService.generateToken("1", "ADMINISTRATOR", "testuser");

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_WithInvalidToken_ShouldReturnFalse() {
        assertFalse(jwtService.isTokenValid("invalid-token"));
    }

    @Test
    void extractUsername_ShouldReturnCorrectSubject() {
        String token = jwtService.generateToken("1", "ADMINISTRATOR", "testuser");

        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void extractUserId_ShouldReturnCorrectUserId() {
        String token = jwtService.generateToken("42", "JUGADOR", "player1");

        assertEquals("42", jwtService.extractUserId(token));
    }

    @Test
    void extractRolId_ShouldReturnCorrectRole() {
        String token = jwtService.generateToken("1", "ADMINISTRATOR", "admin");

        assertEquals("ADMINISTRATOR", jwtService.extractRolId(token));
    }

    @Test
    void extractRole_ShouldReturnCorrectRole() {
        String token = jwtService.generateToken("1", "JUGADOR", "player1");

        assertEquals("JUGADOR", jwtService.extractRole(token));
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewToken() throws Exception {
        String token = jwtService.generateToken("1", "ADMINISTRATOR", "testuser");

        String newToken = jwtService.refreshToken(token);

        assertNotNull(newToken);
        assertTrue(jwtService.isTokenValid(newToken));
        assertEquals("testuser", jwtService.extractUsername(newToken));
        assertEquals("1", jwtService.extractUserId(newToken));
        assertEquals("ADMINISTRATOR", jwtService.extractRolId(newToken));
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldThrowException() {
        assertThrows(Exception.class, () -> jwtService.refreshToken("invalid-token"));
    }

    @Test
    void generateToken_ShouldContainAllClaims() {
        String token = jwtService.generateToken("99", "JUGADOR", "cristiano");

        assertEquals("cristiano", jwtService.extractUsername(token));
        assertEquals("99", jwtService.extractUserId(token));
        assertEquals("JUGADOR", jwtService.extractRolId(token));
    }

    @Test
    void extractEmail_ShouldReturnSameAsUsername() {
        String token = jwtService.generateToken("1", "ADMINISTRATOR", "user@test.com");

        assertEquals("user@test.com", jwtService.extractEmail(token));
        assertEquals(jwtService.extractUsername(token), jwtService.extractEmail(token));
    }
}
