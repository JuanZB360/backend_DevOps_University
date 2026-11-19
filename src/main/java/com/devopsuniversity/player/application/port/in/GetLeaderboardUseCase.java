package com.devopsuniversity.player.application.port.in;

import com.devopsuniversity.player.domain.model.Player;
import java.util.List;

/** Caso de uso: ranking de jugadores por experiencia. */
public interface GetLeaderboardUseCase {

    int MAX_LIMIT = 100;

    /** @param limit número de jugadores (se acota entre 1 y {@link #MAX_LIMIT}) */
    List<Player> top(int limit);
}
