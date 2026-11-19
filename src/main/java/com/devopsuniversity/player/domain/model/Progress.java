package com.devopsuniversity.player.domain.model;

import java.time.Instant;

/**
 * Progreso de un jugador en un momento dado (Value Object inmutable).
 *
 * @param xp                experiencia total acumulada
 * @param level             nivel (calculado por el cliente a partir de la XP)
 * @param points            puntos disponibles para pistas
 * @param completedMissions número de misiones completadas
 * @param saveSnapshot      partida completa en JSON (permite continuar en otro dispositivo); puede ser null
 * @param updatedAt         momento de la última sincronización
 */
public record Progress(int xp, int level, int points, int completedMissions, String saveSnapshot, Instant updatedAt) {

    public Progress {
        if (xp < 0 || level < 1 || points < 0 || completedMissions < 0) {
            throw new IllegalArgumentException("Los valores de progreso no pueden ser negativos (y el nivel mínimo es 1)");
        }
    }

    /** Progreso de un jugador recién registrado. */
    public static Progress initial(Instant now) {
        return new Progress(0, 1, 0, 0, null, now);
    }
}
