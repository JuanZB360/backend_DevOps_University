package com.devopsuniversity.auth.infrastructure.persistence;

import com.devopsuniversity.auth.domain.model.AuthSession;
import com.devopsuniversity.auth.domain.port.SessionRepository;
import com.devopsuniversity.player.domain.model.PlayerId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de salida: implementa {@link SessionRepository} con Spring Data JPA. */
@Component
class SessionPersistenceAdapter implements SessionRepository {

    private final SpringDataSessionRepository jpa;

    SessionPersistenceAdapter(SpringDataSessionRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public AuthSession save(AuthSession session) {
        jpa.save(new SessionJpaEntity(session.tokenHash(), session.playerId().value(),
                session.createdAt(), session.expiresAt()));
        return session;
    }

    @Override
    public Optional<AuthSession> findByTokenHash(String tokenHash) {
        return jpa.findById(tokenHash).map(e -> new AuthSession(
                e.getTokenHash(), new PlayerId(e.getPlayerId()), e.getCreatedAt(), e.getExpiresAt()));
    }

    @Override
    public void deleteByTokenHash(String tokenHash) {
        jpa.deleteById(tokenHash);
    }
}
