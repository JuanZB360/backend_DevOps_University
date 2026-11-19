package com.devopsuniversity.auth.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Fila de la tabla {@code accounts} (ver V2__create_accounts_and_sessions.sql). */
@Entity
@Table(name = "accounts")
class AccountJpaEntity {

    @Id
    @Column(name = "player_id")
    private UUID playerId;

    @Column(nullable = false, unique = true, length = 16)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Requerido por JPA. */
    protected AccountJpaEntity() {
    }

    AccountJpaEntity(UUID playerId, String username, String passwordHash, Instant createdAt) {
        this.playerId = playerId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    UUID getPlayerId() {
        return playerId;
    }

    String getUsername() {
        return username;
    }

    String getPasswordHash() {
        return passwordHash;
    }

    Instant getCreatedAt() {
        return createdAt;
    }
}
