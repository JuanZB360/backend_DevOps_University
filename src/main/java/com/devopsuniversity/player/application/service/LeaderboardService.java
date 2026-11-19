package com.devopsuniversity.player.application.service;

import com.devopsuniversity.player.application.port.in.GetLeaderboardUseCase;
import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.port.PlayerRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Devuelve el ranking de jugadores por experiencia. */
@Service
public class LeaderboardService implements GetLeaderboardUseCase {

    private final PlayerRepository players;

    public LeaderboardService(PlayerRepository players) {
        this.players = players;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Player> top(int limit) {
        return players.findTopByXp(Math.clamp(limit, 1, MAX_LIMIT));
    }
}
