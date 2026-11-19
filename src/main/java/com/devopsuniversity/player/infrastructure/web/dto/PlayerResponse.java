package com.devopsuniversity.player.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import java.time.Instant;

/** Representación pública de un jugador. */
public record PlayerResponse(
        String id,
        String displayName,
        String username,
        Instant createdAt,
        ProgressResponse progress) {

    /**
     * @param saveSnapshot JSON de la partida; se incrusta tal cual (sin escapar) en la respuesta
     */
    public record ProgressResponse(
            int xp,
            int level,
            int points,
            int completedMissions,
            @JsonRawValue String saveSnapshot,
            Instant updatedAt) {
    }
}
