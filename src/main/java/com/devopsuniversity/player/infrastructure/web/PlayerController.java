package com.devopsuniversity.player.infrastructure.web;

import com.devopsuniversity.auth.infrastructure.web.CurrentPlayer;
import com.devopsuniversity.player.application.port.in.GetLeaderboardUseCase;
import com.devopsuniversity.player.application.port.in.GetPlayerUseCase;
import com.devopsuniversity.player.application.port.in.SyncProgressUseCase;
import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerId;
import com.devopsuniversity.player.infrastructure.web.dto.LeaderboardEntryResponse;
import com.devopsuniversity.player.infrastructure.web.dto.PlayerResponse;
import com.devopsuniversity.player.infrastructure.web.dto.SyncProgressRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada HTTP para los casos de uso de jugadores.
 *
 * <p>Los endpoints {@code /me} trabajan siempre con el jugador de la sesión
 * ({@link CurrentPlayer}): nadie puede leer ni modificar la partida de otro.
 * Los jugadores se crean al registrar una cuenta ({@code POST /api/v1/auth/register}).</p>
 */
@RestController
@RequestMapping("/api/v1")
public class PlayerController {

    /** Tamaño máximo aceptado para la partida guardada (2 MB). */
    static final int MAX_SNAPSHOT_CHARS = 2 * 1024 * 1024;

    private final GetPlayerUseCase getPlayer;
    private final SyncProgressUseCase syncProgress;
    private final GetLeaderboardUseCase leaderboard;

    public PlayerController(GetPlayerUseCase getPlayer, SyncProgressUseCase syncProgress,
                            GetLeaderboardUseCase leaderboard) {
        this.getPlayer = getPlayer;
        this.syncProgress = syncProgress;
        this.leaderboard = leaderboard;
    }

    /** Jugador de la sesión con su partida guardada. 401 sin sesión válida. */
    @GetMapping("/me")
    public PlayerResponse me(@CurrentPlayer PlayerId playerId) {
        return PlayerWebMapper.toResponse(getPlayer.get(playerId));
    }

    /** Guarda el progreso. 409 si es más antiguo que el guardado (salvo con {@code replace: true}). */
    @PutMapping("/me/progress")
    public PlayerResponse sync(@CurrentPlayer PlayerId playerId, @Valid @RequestBody SyncProgressRequest request) {
        String snapshot = request.saveSnapshot() == null || request.saveSnapshot().isNull()
                ? null
                : request.saveSnapshot().toString();
        if (snapshot != null && snapshot.length() > MAX_SNAPSHOT_CHARS) {
            throw new IllegalArgumentException("La partida guardada supera el tamaño máximo de 2 MB");
        }
        Player player = syncProgress.sync(new SyncProgressUseCase.Command(
                playerId, request.xp(), request.level(), request.points(),
                request.completedMissions(), snapshot, request.replaceRequested()));
        return PlayerWebMapper.toResponse(player);
    }

    /** Ranking público (sin partidas guardadas ni datos privados). */
    @GetMapping("/leaderboard")
    public List<LeaderboardEntryResponse> leaderboard(@RequestParam(defaultValue = "10") int limit) {
        return PlayerWebMapper.toLeaderboard(leaderboard.top(limit));
    }
}
