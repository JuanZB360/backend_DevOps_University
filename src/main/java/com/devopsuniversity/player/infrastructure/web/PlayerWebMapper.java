package com.devopsuniversity.player.infrastructure.web;

import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.Progress;
import com.devopsuniversity.player.infrastructure.web.dto.LeaderboardEntryResponse;
import com.devopsuniversity.player.infrastructure.web.dto.PlayerResponse;
import java.util.List;
import java.util.stream.IntStream;

/** Convierte el modelo de dominio en DTOs de la API (el dominio nunca sale tal cual). */
public final class PlayerWebMapper {

    private PlayerWebMapper() {
    }

    public static PlayerResponse toResponse(Player player) {
        Progress p = player.getProgress();
        return new PlayerResponse(
                player.getId().toString(),
                player.getName().displayName(),
                player.getName().username(),
                player.getCreatedAt(),
                new PlayerResponse.ProgressResponse(
                        p.xp(), p.level(), p.points(), p.completedMissions(), p.saveSnapshot(), p.updatedAt()));
    }

    static List<LeaderboardEntryResponse> toLeaderboard(List<Player> players) {
        return IntStream.range(0, players.size())
                .mapToObj(i -> {
                    Player player = players.get(i);
                    Progress p = player.getProgress();
                    return new LeaderboardEntryResponse(i + 1, player.getName().displayName(),
                            player.getName().username(), p.level(), p.xp(), p.completedMissions());
                })
                .toList();
    }
}
