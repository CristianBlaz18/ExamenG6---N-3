package com.codigo.ms_usuario.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        // Establece la clave de firma directamente para las pruebas
        String base64Key = "85732b878c0f544da4a863804775ef3914e8ccb82b08820a278302c5b826e291"; // Reemplaza esto con tu clave base64
        jwtService.setKeySignature(base64Key);
    }

    @Test
    void extractUsername() {
        // Arrange
        UserDetails userDetails = User.builder().username("testuser").password("password").build();
        String token = jwtService.generateToken(userDetails);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("testuser", username, "The username should match the one in the token");
    }



    @Test
    void validateToken() {
        // Arrange
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .authorities(new ArrayList<>())
                .build();
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid, "The token should be valid");
    }


    @Test
    void isTokenExpired() {
        // Arrange
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .authorities(new ArrayList<>())
                .build();
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isExpired = jwtService.isTokenExpired(token);

        // Assert
        assertFalse(isExpired, "The token should not be expired");
    }
}