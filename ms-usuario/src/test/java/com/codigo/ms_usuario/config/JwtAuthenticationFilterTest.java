package com.codigo.ms_usuario.config;

import com.codigo.ms_usuario.service.JwtService;
import com.codigo.ms_usuario.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(usuarioService.userDetailsService()).thenReturn(userDetailsService); // Mockeamos el userDetailsService
    }
    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_authenticationSet() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails userDetails = mock(UserDetails.class);
        when(jwtService.extractUsername("validToken")).thenReturn("testUser"); // Mock del username correcto
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails); // Mock del userDetails
        when(jwtService.validateToken("validToken", userDetails)).thenReturn(true); // Token es válido
        when(userDetails.getUsername()).thenReturn("testUser"); // Mock para devolver el username

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals("testUser", authentication.getName()); // Asegúrate de que el nombre es el correcto
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_noToken_noAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Asegúrate de que no se configure la autenticación
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }


    @Test
    void doFilterInternal_invalidToken_noAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalidToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails userDetails = mock(UserDetails.class);

        // Mock del comportamiento cuando el token es inválido
        when(jwtService.extractUsername("invalidToken")).thenReturn("testUser");
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtService.validateToken("invalidToken", userDetails)).thenReturn(false); // Token inválido

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que no haya ninguna autenticación en el contexto
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verificar que la cadena de filtros continúa
        verify(filterChain).doFilter(request, response);
    }

}