package com.codigo.ms_usuario.service.impl;

import com.codigo.ms_usuario.entities.Usuario;
import com.codigo.ms_usuario.respository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioServiceImplTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    private UserDetails userDetails;

    @InjectMocks
    private UsuarioServiceImpl usuarioServiceImpl;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        usuarioServiceImpl = new UsuarioServiceImpl(usuarioRepository);
    }



    @Test
    void userDetailsService_whenUserDoesNotExist() {
        // Arrange
        String email = "test@example.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        UserDetailsService userDetailsService = usuarioServiceImpl.userDetailsService();

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email),
                "Expected UsernameNotFoundException when user does not exist");
    }
}