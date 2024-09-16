package com.codigo.ms_usuario.controller.advice;

import com.codigo.ms_usuario.aggregates.constants.Constants;
import com.codigo.ms_usuario.aggregates.response.UsuarioResponse;
import com.codigo.ms_usuario.controller.personalizado.AuthenticationException;
import com.codigo.ms_usuario.controller.personalizado.IllegalArgumentsException;
import com.codigo.ms_usuario.controller.personalizado.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<UsuarioResponse> controlandoResourceNotFoundException(ResourceNotFoundException resourceNotFoundException){
        UsuarioResponse response = new UsuarioResponse();
        response.setCode(Constants.ERROR_RES_CODE);
        response.setMessage(Constants.ERROR_RES_MESS +" " + resourceNotFoundException.getMessage());
        response.setData(Optional.empty());
        log.error("ERROR DE RESORCENOTFOUNDEXCEPTION ->");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(IllegalArgumentsException.class)
    public ResponseEntity<UsuarioResponse> controlandoIllegalArgumentException(IllegalArgumentsException illegalArgumentsException){
        UsuarioResponse response = new UsuarioResponse();
        response.setCode(Constants.ERROR_DATOS_CODE);
        response.setMessage(Constants.ERROR_DATOS_MESS + illegalArgumentsException.getMessage());
        response.setData(Optional.empty());
        log.error("ERROR DE ILLEGALARGUMENTESEXCEPTION ->");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<UsuarioResponse> controlandoAuthenticationException(AuthenticationException authenticationException){
        UsuarioResponse response = new UsuarioResponse();
        response.setCode(Constants.ERROR_AUTH_CODE);
        response.setMessage(Constants.ERROR_AUTH_MESS + authenticationException.getMessage());
        response.setData(Optional.empty());
        log.error("ERROR DE AUTHENTICATIONEXCEPTIOn ->");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
