package com.codigo.ms_usuario.service.impl;

import com.codigo.ms_usuario.aggregates.constants.Constants;
import com.codigo.ms_usuario.aggregates.request.SignInRequest;
import com.codigo.ms_usuario.aggregates.request.UsuarioRequest;
import com.codigo.ms_usuario.aggregates.request.UsuarioUpdateRequest;

import com.codigo.ms_usuario.aggregates.response.ReniecResponse;
import com.codigo.ms_usuario.aggregates.response.SignInResponse;
import com.codigo.ms_usuario.aggregates.response.UsuarioResponse;
import com.codigo.ms_usuario.client.ReniecClient;
import com.codigo.ms_usuario.controller.personalizado.IllegalArgumentsException;
import com.codigo.ms_usuario.controller.personalizado.ResourceNotFoundException;

import com.codigo.ms_usuario.entities.Rol;
import com.codigo.ms_usuario.entities.Role;
import com.codigo.ms_usuario.entities.Usuario;
import com.codigo.ms_usuario.redis.RedisService;
import com.codigo.ms_usuario.respository.RolRepository;
import com.codigo.ms_usuario.respository.UsuarioRepository;

import com.codigo.ms_usuario.service.JwtService;

import com.codigo.ms_usuario.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class AuthenticationServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisService redisService;

    @Mock
    private ReniecClient reniecClient;
    private UsuarioRequest usuarioRequest;
    private Role role;

    @Value("${token.api}")
    private String tokenapi;
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNumDoc("12345678");
        usuarioRequest.setEmail("test@example.com");
        usuarioRequest.setPassword("password");
        role = Role.USER;// Inicializa los mocks
    }

    @Test
    public void testSignUpUser_EmailDniExist() {
        // Configura los datos de prueba
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNumDoc("71500070");
        usuarioRequest.setEmail("danielblaz59@gmail.com");

        // Configura los mocks
        when(usuarioRepository.existsByNumDoc(anyString())).thenReturn(true);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Llama al método a probar
        ResponseEntity<UsuarioResponse> response = authenticationService.signUpUser(usuarioRequest);

        // Verifica los resultados
        assertEquals(Constants.CODE_EXIST, response.getBody().getCode());
        assertEquals(Constants.MSJ_EXIST_DNI_AND_EMAIL, response.getBody().getMessage());
        assertFalse(response.getBody().getData().isPresent());

    }

    @Test
    public void testSignUpUser_EmailExist() {
        // Configura los datos de prueba
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNumDoc("71500070");
        usuarioRequest.setEmail("danielblaz59@gmail.com");

        // Configura los mocks
        when(usuarioRepository.existsByNumDoc(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Llama al método a probar
        ResponseEntity<UsuarioResponse> response = authenticationService.signUpUser(usuarioRequest);

        // Verifica los resultados
        assertEquals(Constants.CODE_EXIST, response.getBody().getCode());
        assertEquals(Constants.MSJ_EXIST_EMAIL, response.getBody().getMessage());
        assertFalse(response.getBody().getData().isPresent());

    }

    @Test
    public void testSignUpUser_DniExist() {
        // Configura los datos de prueba
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNumDoc("71500070");
        usuarioRequest.setEmail("danielblaz59@gmail.com");

        // Configura los mocks
        when(usuarioRepository.existsByNumDoc(anyString())).thenReturn(true);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        // Llama al método a probar
        ResponseEntity<UsuarioResponse> response = authenticationService.signUpUser(usuarioRequest);

        // Verifica los resultados
        assertEquals(Constants.CODE_EXIST, response.getBody().getCode());
        assertEquals(Constants.MSJ_EXIST_DNI, response.getBody().getMessage());
        assertFalse(response.getBody().getData().isPresent());

    }

    @Test
    public void signUpUser_Success() {
        // Configura los datos de prueba
        UsuarioRequest request = new UsuarioRequest();
        request.setNumDoc("12345678");
        request.setEmail("test@example.com");
        request.setPassword("password");
        Usuario usuario = new Usuario();


        when(usuarioRepository.existsByNumDoc(request.getNumDoc())).thenReturn(false);
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any())).thenReturn(usuario);

        // Llama al método a probar
        ResponseEntity<UsuarioResponse> response = authenticationService.signUpUser(request);

        // Verifica los resultados
        assertNotNull(response);
        assertEquals(Constants.OK_CODE, response.getBody().getCode());
        assertEquals(Constants.OK_MESS, response.getBody().getMessage());
        assertTrue(response.getBody().getData().isPresent());
    }

    @Test
    public void testSignUpAdmin_EmailDniExist() {
        // Configura los datos de prueba
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNumDoc("71500070");
        usuarioRequest.setEmail("danielblaz59@gmail.com");

        // Configura los mocks
        when(usuarioRepository.existsByNumDoc(anyString())).thenReturn(true);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Llama al método a probar
        ResponseEntity<UsuarioResponse> response = authenticationService.signUpAdmin(usuarioRequest);

        // Verifica los resultados
        assertEquals(Constants.CODE_EXIST, response.getBody().getCode());
        assertEquals(Constants.MSJ_EXIST_DNI_AND_EMAIL, response.getBody().getMessage());
        assertFalse(response.getBody().getData().isPresent());
    }

    @Test
    public void testSignUpAdmin_EmailExist() {
        // Configura los datos de prueba
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNumDoc("71500070");
        usuarioRequest.setEmail("danielblaz59@gmail.com");

        // Configura los mocks
        when(usuarioRepository.existsByNumDoc(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Llama al método a probar
        ResponseEntity<UsuarioResponse> response = authenticationService.signUpAdmin(usuarioRequest);

        // Verifica los resultados
        assertEquals(Constants.CODE_EXIST, response.getBody().getCode());
        assertEquals(Constants.MSJ_EXIST_EMAIL, response.getBody().getMessage());
        assertFalse(response.getBody().getData().isPresent());
    }


    @Test
    public void testSignUpAdmin_DniExist() {
        // Configura los datos de prueba
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNumDoc("71500070");
        usuarioRequest.setEmail("danielblaz59@gmail.com");

        // Configura los mocks
        when(usuarioRepository.existsByNumDoc(anyString())).thenReturn(true);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        // Llama al método a probar
        ResponseEntity<UsuarioResponse> response = authenticationService.signUpAdmin(usuarioRequest);

        // Verifica los resultados
        assertEquals(Constants.CODE_EXIST, response.getBody().getCode());
        assertEquals(Constants.MSJ_EXIST_DNI, response.getBody().getMessage());
        assertFalse(response.getBody().getData().isPresent());
    }

    @Test
    public void testSignUpAdmin_Success() {
        // Configura los datos de prueba
        UsuarioRequest request = new UsuarioRequest();
        request.setNumDoc("12345678");
        request.setEmail("test@example.com");
        request.setPassword("password");
        Usuario usuario = new Usuario();

        when(usuarioRepository.existsByNumDoc(request.getNumDoc())).thenReturn(false);
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any())).thenReturn(usuario);

        // Llama al método a probar
        ResponseEntity<UsuarioResponse> response = authenticationService.signUpAdmin(request);

        // Verifica los resultados
        assertNotNull(response);
        assertEquals(Constants.OK_CODE, response.getBody().getCode());
        assertEquals(Constants.OK_MESS, response.getBody().getMessage());
        assertTrue(response.getBody().getData().isPresent());

    }


    @Test
    public void signIn_Success() throws IllegalArgumentsException {
        SignInRequest request = new SignInRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(usuario, null));
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("dummy-token");

        ResponseEntity<UsuarioResponse> response = authenticationService.signIn(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.OK_AUTH_CODE, response.getBody().getCode());
        assertTrue(response.getBody().getData().isPresent());
        assertEquals("dummy-token", ((SignInResponse) response.getBody().getData().get()).getToken());
    }

    @Test
    public void signIn_ExceptionHandling() {
        // Configura los datos de prueba
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("test@example.com");
        signInRequest.setPassword("password");

        // Configura el mock para lanzar una excepción durante la autenticación
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Error en la autenticación"));

        // Llama al método a probar y verifica que la excepción es lanzada
        Exception exception = assertThrows(IllegalArgumentsException.class, () -> {
            authenticationService.signIn(signInRequest);
        });

        // Verifica el mensaje de la excepción
        assertEquals("Error de autenticación: Error en la autenticación", exception.getMessage());
    }

    @Test
    public void listarUsuarios_Success() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.findByEstado(Constants.STATUS_ACTIVE))
                .thenReturn(Optional.of(Collections.singletonList(usuario)));

        ResponseEntity<UsuarioResponse> response = authenticationService.listarUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.OK_CODE, response.getBody().getCode());
        assertTrue(response.getBody().getData().isPresent());
    }

    @Test
    public void listarUsuarios_NullOptional() {
        // Configura el mock para devolver null (esto no es lo típico para Optional)
        when(usuarioRepository.findByEstado(Constants.STATUS_ACTIVE)).thenReturn(null);

        // Llama al método a probar
        ResponseEntity<UsuarioResponse> response = authenticationService.listarUsuarios();

        // Verifica los resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.ERROR_CODE_LIST_EMPTY, response.getBody().getCode());
        assertEquals(Constants.ERROR_MESS_LIST_EMPTY, response.getBody().getMessage());
        assertFalse(response.getBody().getData().isPresent()); // Verifica que no hay datos
    }


    @Test
    public void testListarUsuarioDni_CacheMiss() throws Exception {
        String dni = "12345678";
        Usuario usuario = new Usuario();
        String usuarioJson = objectMapper.writeValueAsString(usuario);

        when(redisService.getCache(dni)).thenReturn(null);
        when(usuarioRepository.findByNumDoc(dni)).thenReturn(Optional.of(usuario));
        doNothing().when(redisService).setCache(dni, usuarioJson, 5);

        ResponseEntity<UsuarioResponse> response = authenticationService.listarUsuarioDni(dni);


        assertNotNull(response.getBody());
        assertEquals(Constants.OK_CODE, response.getBody().getCode());
        assertTrue(response.getBody().getData().isPresent());
        assertEquals(usuario, response.getBody().getData().get());
    }


    @Test
    public void actualizarUsuario_Success() throws ResourceNotFoundException {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setNombres("Updated Name");
        request.setApellidos("Updated Surname");
        request.setEmail("updated@example.com");
        request.setNumDoc("87654321");
        request.setPassword("newpassword");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombres("Old Name");
        usuario.setApellidos("Old Surname");
        usuario.setEmail("old@example.com");
        usuario.setNumDoc("12345678");

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setNombres(request.getNombres());
        updatedUsuario.setApellidos(request.getApellidos());
        updatedUsuario.setEmail(request.getEmail());
        updatedUsuario.setNumDoc(request.getNumDoc());
        updatedUsuario.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(updatedUsuario);

        ResponseEntity<UsuarioResponse> response = authenticationService.actualizarUsuario(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.OK_CODE, response.getBody().getCode());
        assertTrue(response.getBody().getData().isPresent());
    }

    @Test
    public void actualizarUsuario_UsuarioNoEncontrado() throws ResourceNotFoundException {
        // Simular que el usuario no existe en la base de datos
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // Ejecutar el método a probar
        ResponseEntity<UsuarioResponse> response = authenticationService.actualizarUsuario(1L, new UsuarioUpdateRequest());

        // Verificar la respuesta
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Constants.ERROR_RES_CODE, response.getBody().getCode());
        assertFalse(response.getBody().getData().isPresent());
    }


    @Test
    public void eliminarUsuario_Success() throws ResourceNotFoundException {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEstado(Constants.STATUS_ACTIVE);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<UsuarioResponse> response = authenticationService.eliminarUsuario(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.OK_CODE, response.getBody().getCode());
        assertEquals(Constants.OK_MESS, response.getBody().getMessage());
    }

    @Test
    public void eliminarUsuario_UsuarioNoEncontrado() {
        // Simular que el usuario no existe en la base de datos
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        // Ejecutar el método y capturar la excepción
        ResourceNotFoundException thrownException = assertThrows(
                ResourceNotFoundException.class,
                () -> authenticationService.eliminarUsuario(1)
        );

        // Verificar el mensaje de la excepción
        assertEquals("Error no existe el usuario con DNI : 1", thrownException.getMessage());
    }

    @Test
    public void testGetEntityNoReniecResponse() {
        // Arrange
        when(reniecClient.getUsuarioReniec("12345678", "Bearer tokenapi")).thenReturn(null);

        // Act
        Usuario result = authenticationService.getEntity(usuarioRequest, role);

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetRolesRoleNotFound() {
        // Arrange
        when(rolRepository.findByNombreRol(Role.USER.name())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            authenticationService.getRoles(role);
        });
        assertEquals("EL ROL BSUCADO NO EXISTE : USER", thrown.getMessage());
    }


    @Test
    void getEntityUpdate_whenUsuarioIsNull() {
        // Arrange
        UsuarioUpdateRequest usuarioUpdateRequest = new UsuarioUpdateRequest();
        Usuario usuario = null;

        // Act
        Usuario result = authenticationService.getEntityUpdate(usuarioUpdateRequest, usuario);

        // Assert
        assertNull(result, "The result should be null when the input usuario is null");
    }

    @Test
    void executionUsuarioRedis_whenRedisInfoIsNotNull() {
        // Arrange
        String dni = "12345678";
        Usuario usuario = new Usuario();
        // Initialize usuario with necessary data
        usuario.setId(1L);
        usuario.setNombres("John");
        usuario.setApellidos("Doe");
        usuario.setEmail("john.doe@example.com");
        usuario.setNumDoc(dni);
        usuario.setPassword("encodedPassword"); // Assume this is an encoded password
        usuario.setTipoDoc("DNI");
        usuario.setEstado(Constants.STATUS_ACTIVE);
//        usuario.setRoles(Role.USER);


        String redisInfo = Util.convertirToJson(usuario);

        when(redisService.getCache(dni)).thenReturn(redisInfo);

        // Act
        Usuario result = authenticationService.executionUsuarioRedis(dni);

        // Assert
        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
        assertEquals(usuario.getNombres(), result.getNombres());
        assertEquals(usuario.getApellidos(), result.getApellidos());
        assertEquals(usuario.getEmail(), result.getEmail());
        assertEquals(usuario.getNumDoc(), result.getNumDoc());
        assertEquals(usuario.getTipoDoc(), result.getTipoDoc());
        assertEquals(usuario.getEstado(), result.getEstado());
        assertEquals(usuario.getRoles(), result.getRoles());
        assertEquals(usuario.getAuthorities(), result.getAuthorities());
        assertEquals(usuario.isEnabled(), result.isEnabled());
        assertEquals(usuario.isAccountNonExpired(), result.isAccountNonExpired());
        assertEquals(usuario.isAccountNonLocked(), result.isAccountNonLocked());
        assertEquals(usuario.isCredentialsNonExpired(), result.isCredentialsNonExpired());
    }

    @Test
    void getEntity_whenReniecResponseIsNotNull() {
        // Arrange
        String numDoc = "12345678";
        String email = "user@example.com";
        String password = "password";
        Role roleEnum = Role.USER;

        // Crear un UsuarioRequest
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNumDoc(numDoc);
        usuarioRequest.setEmail(email);
        usuarioRequest.setPassword(password);

        // Simular la respuesta de RENIEC
        ReniecResponse reniecResponse = new ReniecResponse();
        reniecResponse.setNombres("John");
        reniecResponse.setApellidoPaterno("Doe");
        reniecResponse.setApellidoMaterno("Smith");
        reniecResponse.setTipoDocumento("DNI");
        reniecResponse.setNumeroDocumento(numDoc);

        // Configurar el mock del rolRepository
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombreRol(roleEnum.name());

        when(authenticationService.executionReniec(numDoc)).thenReturn(reniecResponse);
        when(rolRepository.findByNombreRol(roleEnum.name())).thenReturn(Optional.of(rol));

        // Act
        Usuario result = authenticationService.getEntity(usuarioRequest, roleEnum);

        // Assert
        assertNotNull(result, "The result should not be null when reniecResponse is not null");
        assertEquals("John", result.getNombres(), "The nombres should match");
        assertEquals("Doe Smith", result.getApellidos(), "The apellidos should match");
        assertEquals(Constants.STATUS_ACTIVE, result.getEstado(), "The estado should be active");
        assertEquals(email, result.getEmail(), "The email should match");

        // Verify password encoding
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches(password, result.getPassword()), "The password should be encoded correctly");

        assertEquals("DNI", result.getTipoDoc(), "The tipoDoc should match");
        assertEquals(numDoc, result.getNumDoc(), "The numDoc should match");
        assertTrue(result.getIsAccountNonExpired(), "Account should not be expired");
        assertTrue(result.getIsAccountNonLocked(), "Account should be locked");
        assertTrue(result.getIsCredentialsNonExpired(), "Credentials should not be expired");
        assertTrue(result.getIsEnabled(), "Account should be enabled");
        assertNotNull(result.getRoles(), "Roles should not be null");
        assertFalse(result.getRoles().isEmpty(), "Roles should not be empty");
        assertTrue(result.getRoles().contains(rol), "The roles should contain the expected role");
    }



}