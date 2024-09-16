package com.codigo.ms_usuario.service;

import com.codigo.ms_usuario.aggregates.request.SignInRequest;
import com.codigo.ms_usuario.aggregates.request.UsuarioRequest;
import com.codigo.ms_usuario.aggregates.request.UsuarioUpdateRequest;
import com.codigo.ms_usuario.aggregates.response.UsuarioResponse;

import com.codigo.ms_usuario.controller.personalizado.IllegalArgumentsException;
import com.codigo.ms_usuario.controller.personalizado.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    //SIGNUP -> INSCRIBIRSE
    ResponseEntity<UsuarioResponse> signUpUser(UsuarioRequest signUpRequest);
    ResponseEntity<UsuarioResponse> signUpAdmin(UsuarioRequest signUpRequest);
    //SIGNIN -> INICIAR SESION
    ResponseEntity<UsuarioResponse> signIn(SignInRequest signInRequest) throws IllegalArgumentsException;

    //METODOS GET UPDATE Y DELETE

    ResponseEntity<UsuarioResponse> listarUsuarios();
    ResponseEntity<UsuarioResponse> listarUsuarioDni(String dni) ;
    ResponseEntity<UsuarioResponse> actualizarUsuario(Long id, UsuarioUpdateRequest usuarioUpdateRequest) throws ResourceNotFoundException;
    ResponseEntity<UsuarioResponse> eliminarUsuario(int id) throws ResourceNotFoundException;
}
