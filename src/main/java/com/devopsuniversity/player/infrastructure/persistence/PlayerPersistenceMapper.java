package com.devopsuniversity.player.infrastructure.persistence;

import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerId;
import com.devopsuniversity.player.domain.model.PlayerName;
import com.devopsuniversity.player.domain.model.Progress;

/** Convierte entre el modelo de dominio y la entidad JPA. */
final class PlayerPersistenceMapper {

    private PlayerPersistenceMapper() {
    }

    static PlayerJpaEntity toEntity(Player player) {
        Progress progress = player.getProgress();
        return new PlayerJpaEntity(
                player.getId().value(),
                player.getName().displayName(),
                player.getName().username(),
                player.getCreatedAt(),
                progress.xp(),
                progress.level(),
                progress.points(),
                progress.completedMissions(),
                progress.saveSnapshot(),
                progress.updatedAt());
    }

    static Player toDomain(PlayerJpaEntity entity) {
        return Player.restore(
                new PlayerId(entity.getId()),
                new PlayerName(entity.getDisplayName(), entity.getUsername()),
                entity.getCreatedAt(),
                new Progress(entity.getXp(), entity.getLevel(), entity.getPoints(),
                        entity.getCompletedMissions(), entity.getSaveSnapshot(), entity.getUpdatedAt()));
    }
}
