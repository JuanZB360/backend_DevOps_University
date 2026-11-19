package com.devopsuniversity.auth.application.port.in;

/** Caso de uso: cerrar la sesión asociada a un token. */
public interface LogoutUseCase {

    /** Idempotente: cerrar una sesión que ya no existe no es un error. */
    void logout(String token);
}
