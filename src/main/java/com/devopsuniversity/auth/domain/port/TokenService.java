package com.devopsuniversity.auth.domain.port;

/** Puerto para generar tokens de sesión y calcular su hash. */
public interface TokenService {

    /** Token aleatorio criptográficamente seguro (es lo que recibe el cliente). */
    String generate();

    /** Hash determinista del token (es lo que se guarda en la base de datos). */
    String hash(String token);
}
