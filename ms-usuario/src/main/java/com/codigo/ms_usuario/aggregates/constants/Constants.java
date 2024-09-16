package com.codigo.ms_usuario.aggregates.constants;

public class Constants {
    public static final Boolean ESTADO_ACTIVO=true;
    public static final String CLAIM_ROLE = "rol";

    //RUTAS PERMITIDAS
    public static final String ENPOINTS_PERMIT_LOGIN = "/api/v1/users/signin";
    public static final String ENPOINTS_SIGNUP_USER = "/api/v1/users/signupuser";
    public static final String ENPOINTS_SIGNUP_ADMIN= "/api/v1/users/signupadmin";
    //RUTAS NO PERMITIDAS
    public static final String ENPOINTS_NO_PERMITE= "/api/v1/users/**";

    public static final String CLAVE_AccountNonExpired ="isAccountNonExpired";
    public static final String CLAVE_AccountNonLocked ="isAccountNonLocked";
    public static final String CLAVE_CredentialsNonExpired = "isCredentialsNonExpired";
    public static final String CLAVE_Enabled = "isEnabled";


    public static final Integer OK_CODE = 2000;
    public static final String OK_MESS = "EJECUTADO SIN PROBLEMAS!";

    //REGISTRARSE
    public static final Integer CODE_EXIST=1001;
    public static final String MSJ_EXIST_DNI_AND_EMAIL="El DNI y EL CORREO YA EXISTE";
    public static final String MSJ_EXIST_DNI="El DNI YA EXISTE";
    public static final String MSJ_EXIST_EMAIL="EL CORREO YA EXISTE";




    public static final Integer ERROR_DNI_CODE = 2004;
    public static final String ERROR_DNI_MESS = "ERROR CON EL DNI";

    public static final Integer ERROR_RES_CODE = 4009;
    public static final String ERROR_RES_MESS = "ERROR EL RECURSO SOLICITADO NO FUE ENCONTRADO";

    public static final Integer ERROR_DATOS_CODE = 4010;
    public static final String ERROR_DATOS_MESS = "ERROR LOS DATOS PROPORCIONADOS NO SON VALIDOS." +
            "POR FAVOR ,VERIFICA LA INFORMACION ENVIADA";


    public static final Integer ERROR_AUTH_CODE = 4006;
    public static final String ERROR_AUTH_MESS = "ERROR AUTENTICACION FALLIDA. EL TOKEN PROPORCIONADO NO ES VALIDO O HA EXPIRADO";
    public static final Integer ERROR_CODE_LIST_EMPTY= 2009;
    public static final String ERROR_MESS_LIST_EMPTY = "NO HAY REGISTROS!!";

    public static final Integer OK_AUTH_CODE = 2001;
    public static final String OK_AUTH_MESS = "CREDENCIALES CORRECTAS";

    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_INACTIVE = 0;
}
