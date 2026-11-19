package com.devopsuniversity.player.application.port.in;

import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerId;

/** Caso de uso: guardar en el servidor el progreso del jugador (sincronización entre dispositivos). */
public interface SyncProgressUseCase {

    /** Datos de entrada del caso de uso. */
    /**
     * @param replace {@code true} para reemplazar el progreso aunque tenga menos XP
     *                (reiniciar partida o importar una copia); {@code false} en el guardado normal
     */
    record Command(PlayerId playerId, int xp, int level, int points, int completedMissions, String saveSnapshot,
                   boolean replace) {
    }

    /**
     * @throws com.devopsuniversity.player.domain.exception.PlayerNotFoundException si el jugador no existe
     * @throws com.devopsuniversity.player.domain.exception.StaleProgressException  si el progreso es antiguo
     */
    Player sync(Command command);
}
