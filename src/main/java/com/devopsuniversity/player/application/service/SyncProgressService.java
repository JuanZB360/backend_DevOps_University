package com.devopsuniversity.player.application.service;

import com.devopsuniversity.player.application.port.in.SyncProgressUseCase;
import com.devopsuniversity.player.domain.exception.PlayerNotFoundException;
import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.Progress;
import com.devopsuniversity.player.domain.port.PlayerRepository;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sincroniza el progreso. La regla de "no retroceder" la aplica el dominio
 * ({@link Player#syncProgress}); este servicio solo orquesta.
 */
@Service
public class SyncProgressService implements SyncProgressUseCase {

    private final PlayerRepository players;
    private final Clock clock;

    public SyncProgressService(PlayerRepository players, Clock clock) {
        this.players = players;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Player sync(Command command) {
        Player player = players.findById(command.playerId())
                .orElseThrow(() -> new PlayerNotFoundException(command.playerId()));
        Progress incoming = new Progress(
                command.xp(), command.level(), command.points(), command.completedMissions(),
                command.saveSnapshot(), clock.instant());
        if (command.replace()) {
            player.replaceProgress(incoming);
        } else {
            player.syncProgress(incoming);
        }
        return players.save(player);
    }
}
