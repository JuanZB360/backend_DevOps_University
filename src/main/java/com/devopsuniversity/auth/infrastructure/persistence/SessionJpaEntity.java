package com.devopsuniversity.auth.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Fila de la tabla {@code auth_sessions} (ver V2__create_accounts_and_sessions.sql). */
@Entity
@Table(name = "auth_sessions")
class SessionJpaEntity {

    @Id
    @Column(name = "token_hash", length = 64)
    private String tokenHash;

    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /** Requerido por JPA. */
    protected SessionJpaEntity() {
    }

    SessionJpaEntity(String tokenHash, UUID playerId, Instant createdAt, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.playerId = playerId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    String getTokenHash() {
        return tokenHash;
    }

    UUID getPlayerId() {
        return playerId;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    Instant getExpiresAt() {
        return expiresAt;
    }
}
