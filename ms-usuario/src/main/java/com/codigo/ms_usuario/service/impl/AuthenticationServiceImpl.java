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
import com.codigo.ms_usuario.service.AuthenticationService;
import com.codigo.ms_usuario.service.JwtService;
import com.codigo.ms_usuario.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service

public class AuthenticationServiceImpl implements AuthenticationService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final ReniecClient reniecClient;


    @Value("${token.api}")
    private String tokenapi;

    public AuthenticationServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, AuthenticationManager authenticationManager, JwtService jwtService, RedisService redisService, ReniecClient reniecClient) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.reniecClient = reniecClient;
    }

    @Override
    public ResponseEntity<UsuarioResponse> signUpUser(UsuarioRequest signUpRequest) {
        boolean existDni = usuarioRepository.existsByNumDoc(signUpRequest.getNumDoc());
        boolean existEmail = usuarioRepository.existsByEmail(signUpRequest.getEmail());

        if (existDni && existEmail) {
            // Mensaje si ambos DNI y correo ya existen
            UsuarioResponse response = new UsuarioResponse(Constants.CODE_EXIST,
                    Constants.MSJ_EXIST_DNI_AND_EMAIL, Optional.empty());
            return ResponseEntity.ok(response);
        } else if (existDni) {
            // Mensaje si solo el DNI ya existe
            UsuarioResponse response = new UsuarioResponse(Constants.CODE_EXIST,
                    Constants.MSJ_EXIST_DNI, Optional.empty());
            return ResponseEntity.ok(response);
        } else if (existEmail) {
            // Mensaje si solo el correo ya existe
            UsuarioResponse response = new UsuarioResponse(Constants.CODE_EXIST,
                    Constants.MSJ_EXIST_EMAIL, Optional.empty());
            return ResponseEntity.ok(response);
        } else {
            Usuario usuario = usuarioRepository.save(getEntity(signUpRequest,Role.USER));
            UsuarioResponse responsenew = new UsuarioResponse(Constants.OK_CODE, Constants.OK_MESS,
                    Optional.of(usuario));
            return  ResponseEntity.ok(responsenew);
        }
    }


    @Override
    public ResponseEntity<UsuarioResponse> signUpAdmin(UsuarioRequest signUpRequest) {
        boolean existDni = usuarioRepository.existsByNumDoc(signUpRequest.getNumDoc());
        boolean existEmail = usuarioRepository.existsByEmail(signUpRequest.getEmail());

        if (existDni && existEmail) {
            // Mensaje si ambos DNI y correo ya existen
            UsuarioResponse response = new UsuarioResponse(Constants.CODE_EXIST,
                    Constants.MSJ_EXIST_DNI_AND_EMAIL, Optional.empty());
            return ResponseEntity.ok(response);
        } else if (existDni) {
            // Mensaje si solo el DNI ya existe
            UsuarioResponse response = new UsuarioResponse(Constants.CODE_EXIST,
                    Constants.MSJ_EXIST_DNI, Optional.empty());
            return ResponseEntity.ok(response);
        } else if (existEmail) {
            // Mensaje si solo el correo ya existe
            UsuarioResponse response = new UsuarioResponse(Constants.CODE_EXIST,
                    Constants.MSJ_EXIST_EMAIL, Optional.empty());
            return ResponseEntity.ok(response);
        } else {
            Usuario usuario = usuarioRepository.save(getEntity(signUpRequest,Role.ADMIN));
            UsuarioResponse responsenew = new UsuarioResponse(Constants.OK_CODE, Constants.OK_MESS,
                    Optional.of(usuario));
            return  ResponseEntity.ok(responsenew);

        }

    }

    public Usuario getEntity(UsuarioRequest usuarioRequest, Role role){
        Usuario usuario = new Usuario();

        //Ejecutar la consulta;
        ReniecResponse reniecResponse = executionReniec(usuarioRequest.getNumDoc());

        if (Objects.nonNull(reniecResponse)){
            usuario.setNombres(reniecResponse.getNombres());
            usuario.setApellidos(reniecResponse.getApellidoPaterno() + " " + reniecResponse.getApellidoMaterno());
            usuario.setEstado(Constants.STATUS_ACTIVE);
            usuario.setEmail(usuarioRequest.getEmail());
            usuario.setPassword(new BCryptPasswordEncoder().encode(usuarioRequest.getPassword()));
            usuario.setTipoDoc(reniecResponse.getTipoDocumento());
            usuario.setNumDoc(reniecResponse.getNumeroDocumento());
            usuario.setIsAccountNonExpired(true);
            usuario.setIsAccountNonLocked(true);
            usuario.setIsCredentialsNonExpired(true);
            usuario.setIsEnabled(true);
            usuario.setRoles(Collections.singleton(getRoles(role)));
            return usuario;
        }
        return null;
    }

    @Override
    public ResponseEntity<UsuarioResponse> signIn(SignInRequest signInRequest) throws IllegalArgumentsException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInRequest.getEmail(),signInRequest.getPassword()));
            var user = usuarioRepository.findByEmail(signInRequest.getEmail())
                    .orElseThrow(()-> new IllegalArgumentException("ERROR USUARIO NO ENCONTRADO"));
            var token = jwtService.generateToken(user);
            SignInResponse response = new SignInResponse();
            response.setToken(token);
            UsuarioResponse response1 = new UsuarioResponse(Constants.OK_AUTH_CODE,Constants.OK_AUTH_MESS,
                    Optional.of(response));
            return new ResponseEntity<>(response1, HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentsException("Error de autenticación: " + e.getMessage());
        }

    }

    @Override
    public ResponseEntity<UsuarioResponse> listarUsuarios() {
        Optional<List<Usuario>> usuarioEntityList = usuarioRepository.findByEstado(Constants.STATUS_ACTIVE);
        if(Objects.nonNull(usuarioEntityList)){
            UsuarioResponse response1 = new UsuarioResponse(Constants.OK_CODE,Constants.OK_MESS,
                    usuarioEntityList);
            return new ResponseEntity<>(response1, HttpStatus.OK);
        }else {
            UsuarioResponse response = new UsuarioResponse(Constants.ERROR_CODE_LIST_EMPTY
                    ,Constants.ERROR_MESS_LIST_EMPTY, Optional.empty());
            return ResponseEntity.ok(response);
        }
    }

    @Override
    public ResponseEntity<UsuarioResponse> listarUsuarioDni(String dni){
        Usuario usuarioresponse = executionUsuarioRedis(dni);
        UsuarioResponse response1 = new UsuarioResponse(Constants.OK_CODE, Constants.OK_MESS,
                Optional.of(usuarioresponse));
        return ResponseEntity.ok(response1);




    }
    public Usuario executionUsuarioRedis(String dni)  {
        String redisInfo = redisService.getCache(dni);

        if(redisInfo != null){
            return Util.convertFromJson(redisInfo,Usuario.class);
        }else{
            Optional<Usuario> usuario = usuarioRepository.findByNumDoc(dni);
            String dataForRedis = Util.convertirToJson(usuario.get());
            redisService.setCache(dni,dataForRedis,5);
            return usuario.get();
        }
    }


    @Override
    public ResponseEntity<UsuarioResponse> actualizarUsuario(Long id, UsuarioUpdateRequest usuarioUpdateRequest) throws ResourceNotFoundException {
        if(usuarioRepository.existsById(id)){
            Usuario usuarioRecuperado = usuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Error no existe el usuario con DNI : " + id));
            Usuario actualizar = getEntityUpdate(usuarioUpdateRequest,usuarioRecuperado);
            UsuarioResponse response1 = new UsuarioResponse(Constants.OK_CODE,Constants.OK_MESS,
                    Optional.of(usuarioRepository.save(actualizar)));
            return new ResponseEntity<>(response1, HttpStatus.OK);
        }else{
            UsuarioResponse response1 = new UsuarioResponse(Constants.ERROR_RES_CODE,Constants.ERROR_RES_MESS,
                    Optional.empty());
            return new ResponseEntity<>(response1, HttpStatus.NOT_FOUND);
        }

    }

    public Usuario getEntityUpdate(UsuarioUpdateRequest usuarioUpdateRequest,Usuario usuario){
        if(usuario != null){
            usuario.setNombres(usuarioUpdateRequest.getNombres());
            usuario.setApellidos(usuarioUpdateRequest.getApellidos());
            usuario.setEmail(usuarioUpdateRequest.getEmail());
            usuario.setNumDoc(usuarioUpdateRequest.getNumDoc());
            usuario.setPassword(new BCryptPasswordEncoder().encode(usuarioUpdateRequest.getPassword()));
        }else {
            return  null;
        }
        return usuario;
    }

    @Override
    public ResponseEntity<UsuarioResponse> eliminarUsuario(int id) throws ResourceNotFoundException {
        UsuarioResponse response = new UsuarioResponse();
        Usuario usuarioRecuparado = usuarioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Error no existe el usuario con DNI : " + id));

        usuarioRecuparado.setEstado(Constants.STATUS_INACTIVE);
        response.setCode(Constants.OK_CODE);
        response.setMessage(Constants.OK_MESS);
        response.setData(Optional.of(usuarioRepository.save(usuarioRecuparado)));
        return ResponseEntity.ok(response);


    }








    public ReniecResponse executionReniec(String documento){
        String auth = "Bearer "+tokenapi;
        ReniecResponse response = reniecClient.getUsuarioReniec(documento,auth);
        return response;
    }




    public Rol getRoles(Role rolBuscado){
        return rolRepository.findByNombreRol(rolBuscado.name())
                .orElseThrow(() -> new RuntimeException("EL ROL BSUCADO NO EXISTE : "
                        + rolBuscado.name()));
    }
}
