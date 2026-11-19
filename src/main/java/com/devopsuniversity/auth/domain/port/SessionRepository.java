package com.devopsuniversity.auth.domain.port;

import com.devopsuniversity.auth.domain.model.AuthSession;
import java.util.Optional;

/** Puerto de salida: almacén de sesiones abiertas. */
public interface SessionRepository {

    AuthSession save(AuthSession session);

    Optional<AuthSession> findByTokenHash(String tokenHash);

    /** Cierra la sesión. No falla si ya no existía (cerrar sesión es idempotente). */
    void deleteByTokenHash(String tokenHash);
}
