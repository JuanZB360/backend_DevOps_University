package com.devopsuniversity.auth.application.port.in;

import com.devopsuniversity.player.domain.model.PlayerId;

/** Caso de uso: averiguar a qué jugador pertenece un token de sesión. */
public interface AuthenticateUseCase {

    /** @throws com.devopsuniversity.auth.domain.exception.UnauthenticatedException si el token no es válido o caducó */
    PlayerId authenticate(String token);
}
