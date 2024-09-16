package com.codigo.ms_usuario.aggregates.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioUpdateRequest {
    private String nombres;
    private String apellidos;
    private String email;
    private String numDoc;
    private String password;


}
