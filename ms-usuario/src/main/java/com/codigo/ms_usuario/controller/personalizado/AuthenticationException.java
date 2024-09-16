package com.codigo.ms_usuario.controller.personalizado;

public class AuthenticationException extends Exception{
    public AuthenticationException(String mensaje){
        super(mensaje);
    }
}
