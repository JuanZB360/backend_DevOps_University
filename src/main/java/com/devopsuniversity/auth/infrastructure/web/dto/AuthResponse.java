package com.devopsuniversity.auth.infrastructure.web.dto;

import com.devopsuniversity.player.infrastructure.web.dto.PlayerResponse;
import java.time.Instant;

/**
 * Respuesta al registrarse o iniciar sesión.
 *
 * @param token     enviar en cada petición como {@code Authorization: Bearer <token>}
 * @param expiresAt caducidad de la sesión
 * @param player    jugador con su partida guardada
 */
public record AuthResponse(String token, Instant expiresAt, PlayerResponse player) {
}
