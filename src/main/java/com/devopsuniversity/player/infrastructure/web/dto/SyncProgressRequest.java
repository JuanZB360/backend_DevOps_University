package com.devopsuniversity.player.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import tools.jackson.databind.JsonNode;

/**
 * Cuerpo de {@code PUT /api/v1/me/progress}.
 *
 * @param saveSnapshot partida completa tal como la guarda el frontend (opcional)
 * @param replace      {@code true} para sustituir el progreso aunque tenga menos XP (reiniciar o importar)
 */
public record SyncProgressRequest(
        @Min(value = 0, message = "La XP no puede ser negativa") int xp,
        @Min(value = 1, message = "El nivel mínimo es 1") int level,
        @Min(value = 0, message = "Los puntos no pueden ser negativos") int points,
        @Min(value = 0, message = "Las misiones completadas no pueden ser negativas") int completedMissions,
        JsonNode saveSnapshot,
        Boolean replace) {

    /** {@code replace} es opcional: si no se envía, es un guardado normal. */
    public boolean replaceRequested() {
        return Boolean.TRUE.equals(replace);
    }
}
