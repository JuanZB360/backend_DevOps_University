package com.devopsuniversity.player.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Representación del jugador en la base de datos.
 *
 * <p>Es distinta del modelo de dominio {@code Player} a propósito: así los
 * detalles de JPA (constructor vacío, setters, anotaciones) no contaminan
 * el dominio. La conversión la hace {@link PlayerPersistenceMapper}.</p>
 */
@Entity
@Table(name = "players")
public class PlayerJpaEntity {

    @Id
    private UUID id;

    @Column(name = "display_name", nullable = false, length = 24)
    private String displayName;

    @Column(nullable = false, length = 16)
    private String username;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private int xp;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int points;

    @Column(name = "completed_missions", nullable = false)
    private int completedMissions;

    /** JSON de la partida. TEXT no tiene límite práctico en PostgreSQL. */
    @Column(name = "save_snapshot", columnDefinition = "text")
    private String saveSnapshot;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Requerido por JPA. */
    protected PlayerJpaEntity() {
    }

    @SuppressWarnings("java:S107") // Entidad plana: un parámetro por columna.
    PlayerJpaEntity(UUID id, String displayName, String username, Instant createdAt, int xp, int level,
                    int points, int completedMissions, String saveSnapshot, Instant updatedAt) {
        this.id = id;
        this.displayName = displayName;
        this.username = username;
        this.createdAt = createdAt;
        this.xp = xp;
        this.level = level;
        this.points = points;
        this.completedMissions = completedMissions;
        this.saveSnapshot = saveSnapshot;
        this.updatedAt = updatedAt;
    }

    UUID getId() {
        return id;
    }

    String getDisplayName() {
        return displayName;
    }

    String getUsername() {
        return username;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    int getXp() {
        return xp;
    }

    int getLevel() {
        return level;
    }

    int getPoints() {
        return points;
    }

    int getCompletedMissions() {
        return completedMissions;
    }

    String getSaveSnapshot() {
        return saveSnapshot;
    }

    Instant getUpdatedAt() {
        return updatedAt;
    }
}
