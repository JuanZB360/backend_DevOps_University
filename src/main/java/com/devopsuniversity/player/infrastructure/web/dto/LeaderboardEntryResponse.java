package com.devopsuniversity.player.infrastructure.web.dto;

/** Fila del ranking (sin datos privados como la partida guardada). */
public record LeaderboardEntryResponse(
        int position,
        String displayName,
        String username,
        int level,
        int xp,
        int completedMissions) {
}
