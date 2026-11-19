package com.devopsuniversity.auth.application.port.in;

import com.devopsuniversity.player.domain.model.Player;
import java.time.Instant;

/**
 * Resultado de registrarse o iniciar sesión.
 *
 * @param token     token de sesión en claro (solo se entrega una vez, al cliente)
 * @param expiresAt caducidad de la sesión
 * @param player    jugador con su progreso guardado (para continuar la partida)
 */
public record AuthResult(String token, Instant expiresAt, Player player) {
}
