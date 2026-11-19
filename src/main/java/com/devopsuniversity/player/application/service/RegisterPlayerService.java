package com.devopsuniversity.player.application.service;

import com.devopsuniversity.player.application.port.in.RegisterPlayerUseCase;
import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerName;
import com.devopsuniversity.player.domain.port.PlayerRepository;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Registra jugadores: valida el nombre (en el dominio) y lo persiste. */
@Service
public class RegisterPlayerService implements RegisterPlayerUseCase {

    private final PlayerRepository players;
    private final Clock clock;

    public RegisterPlayerService(PlayerRepository players, Clock clock) {
        this.players = players;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Player register(Command command) {
        Player player = Player.register(PlayerName.of(command.displayName()), clock.instant());
        return players.save(player);
    }
}
