package com.codigo.ms_usuario.controller;

import com.codigo.ms_usuario.aggregates.constants.Constants;
import com.codigo.ms_usuario.aggregates.request.SignInRequest;
import com.codigo.ms_usuario.aggregates.request.UsuarioRequest;
import com.codigo.ms_usuario.aggregates.request.UsuarioUpdateRequest;
import com.codigo.ms_usuario.aggregates.response.UsuarioResponse;
import com.codigo.ms_usuario.controller.personalizado.AuthenticationException;
import com.codigo.ms_usuario.controller.personalizado.IllegalArgumentsException;
import com.codigo.ms_usuario.controller.personalizado.ResourceNotFoundException;
import com.codigo.ms_usuario.service.AuthenticationService;
import com.codigo.ms_usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;
    @PostMapping("/signupuser")
    public ResponseEntity<UsuarioResponse> signUpUser(  @RequestBody UsuarioRequest signUpRequest){
        return authenticationService.signUpUser(signUpRequest);
    }
    @PostMapping("/signupadmin")
    public ResponseEntity<UsuarioResponse> signUpAdmin(@RequestBody UsuarioRequest signUpRequest){
        return authenticationService.signUpAdmin(signUpRequest);
    }

    @PostMapping("/signin")
    public ResponseEntity<UsuarioResponse> signin(@RequestBody SignInRequest signInRequest) throws IllegalArgumentsException {
        return authenticationService.signIn(signInRequest);
    }

    @GetMapping()
    public ResponseEntity<UsuarioResponse> listarUsuarios() {
        UsuarioResponse response = authenticationService.listarUsuarios().getBody();
        if (response.getCode().equals(Constants.OK_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{dni}")
    public ResponseEntity<UsuarioResponse> listarUsuarioDni(@PathVariable("dni") String dni) throws ResourceNotFoundException {
       UsuarioResponse response = authenticationService.listarUsuarioDni(dni).getBody();
       if(response.getCode().equals(Constants.OK_CODE)){
           return new ResponseEntity<>(response, HttpStatus.OK);
       }else{
           return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
       }

    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable("id") Long id, @RequestBody UsuarioUpdateRequest usuarioUpdateRequest) throws ResourceNotFoundException {
        UsuarioResponse response = authenticationService.actualizarUsuario(id,usuarioUpdateRequest).getBody();
        if(response.getCode().equals(Constants.OK_CODE)){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse> eliminarUsuario(@PathVariable("id") int id) throws ResourceNotFoundException {
        UsuarioResponse response = authenticationService.eliminarUsuario(id).getBody();
        if(response.getCode().equals(Constants.OK_CODE)){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


}
