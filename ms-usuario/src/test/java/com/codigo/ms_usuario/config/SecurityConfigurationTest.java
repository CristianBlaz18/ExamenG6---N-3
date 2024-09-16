package com.codigo.ms_usuario.config;

import com.codigo.ms_usuario.aggregates.constants.Constants;
import com.codigo.ms_usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import org.springframework.test.web.servlet.MockMvc;

import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;





class SecurityConfigurationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private SecurityConfiguration securityConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void authenticationProvider() {
        AuthenticationProvider provider = securityConfiguration.authenticationProvider();


        assertTrue(provider instanceof DaoAuthenticationProvider);


        assertNotNull(provider);
    }

    @Test
    void passwordEncoder() {
        PasswordEncoder passwordEncoder = securityConfiguration.passwordEncoder();

        // Verificamos que el PasswordEncoder es del tipo BCryptPasswordEncoder
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }


}