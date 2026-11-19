package com.devopsuniversity.auth.domain.model;

import com.devopsuniversity.player.domain.model.PlayerId;
import java.time.Instant;
import java.util.Objects;

/**
 * Cuenta de acceso de un jugador: une sus credenciales con su {@code Player}.
 *
 * <p>Se separa del jugador a propósito (Responsabilidad Única): el módulo
 * {@code player} no sabe nada de contraseñas y el módulo {@code auth} no sabe
 * nada de XP ni de misiones.</p>
 *
 * @param playerId     jugador al que pertenece la cuenta (relación 1 a 1)
 * @param username     usuario con el que se inicia sesión
 * @param passwordHash hash BCrypt de la contraseña (nunca la contraseña en claro)
 * @param createdAt    fecha de alta
 */
public record Account(PlayerId playerId, Username username, String passwordHash, Instant createdAt) {

    public Account {
        Objects.requireNonNull(playerId);
        Objects.requireNonNull(username);
        Objects.requireNonNull(passwordHash);
        Objects.requireNonNull(createdAt);
    }
}
