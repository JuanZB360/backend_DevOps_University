package com.devopsuniversity.auth.application.port.in;

/** Caso de uso: iniciar sesión con usuario y contraseña. */
public interface LoginUseCase {

    record Command(String username, String password) {
    }

    /** @throws com.devopsuniversity.auth.domain.exception.InvalidCredentialsException si no coinciden */
    AuthResult login(Command command);
}
