package com.devopsuniversity.auth.application.port.in;

/** Caso de uso: crear una cuenta (usuario + contraseña) y dejar la sesión iniciada. */
public interface RegisterAccountUseCase {

    record Command(String username, String password) {
    }

    /**
     * @throws com.devopsuniversity.auth.domain.exception.InvalidCredentialsFormatException si no cumplen las reglas
     * @throws com.devopsuniversity.auth.domain.exception.UsernameTakenException            si el usuario ya existe
     */
    AuthResult register(Command command);
}
