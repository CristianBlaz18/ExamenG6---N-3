package com.codigo.ms_usuario.aggregates.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequest {
    private String numDoc;
    private String email;
    private String password;


}
