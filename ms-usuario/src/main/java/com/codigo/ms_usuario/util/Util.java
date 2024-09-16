package com.codigo.ms_usuario.util;

import com.codigo.ms_usuario.aggregates.response.UsuarioResponse;
import com.codigo.ms_usuario.entities.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
    public static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    public static String convertirToJson(Usuario usuario){

        try {
            return objectMapper.writeValueAsString(usuario);
        }catch (JsonProcessingException e){
            throw  new RuntimeException(e);
        }
    }

    public static <T> T convertFromJson(String json, Class<T> tipoClase){

        try{
            return objectMapper.readValue(json,tipoClase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
