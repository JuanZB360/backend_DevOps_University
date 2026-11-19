package com.devopsuniversity.auth.domain.model;

import com.devopsuniversity.player.domain.model.PlayerId;
import java.time.Instant;
import java.util.Objects;

/**
 * Sesión abierta (un dispositivo o navegador con la sesión iniciada).
 *
 * <p>Solo se guarda el HASH del token: si alguien leyera la base de datos no
 * podría usar los tokens para suplantar a nadie.</p>
 *
 * @param tokenHash SHA-256 (hex) del token que tiene el cliente
 * @param playerId  dueño de la sesión
 * @param createdAt inicio de la sesión
 * @param expiresAt caducidad; después hay que volver a iniciar sesión
 */
public record AuthSession(String tokenHash, PlayerId playerId, Instant createdAt, Instant expiresAt) {

    public AuthSession {
        Objects.requireNonNull(tokenHash);
        Objects.requireNonNull(playerId);
        Objects.requireNonNull(createdAt);
        Objects.requireNonNull(expiresAt);
    }

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }
}
