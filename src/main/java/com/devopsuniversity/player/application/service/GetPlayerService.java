package com.devopsuniversity.player.application.service;

import com.devopsuniversity.player.application.port.in.GetPlayerUseCase;
import com.devopsuniversity.player.domain.exception.PlayerNotFoundException;
import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerId;
import com.devopsuniversity.player.domain.port.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta un jugador por id. */
@Service
public class GetPlayerService implements GetPlayerUseCase {

    private final PlayerRepository players;

    public GetPlayerService(PlayerRepository players) {
        this.players = players;
    }

    @Override
    @Transactional(readOnly = true)
    public Player get(PlayerId id) {
        return players.findById(id).orElseThrow(() -> new PlayerNotFoundException(id));
    }
}
