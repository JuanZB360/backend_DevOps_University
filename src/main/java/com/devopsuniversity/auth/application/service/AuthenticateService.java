package com.devopsuniversity.auth.application.service;

import com.devopsuniversity.auth.application.port.in.AuthenticateUseCase;
import com.devopsuniversity.auth.domain.exception.UnauthenticatedException;
import com.devopsuniversity.auth.domain.model.AuthSession;
import com.devopsuniversity.auth.domain.port.SessionRepository;
import com.devopsuniversity.auth.domain.port.TokenService;
import com.devopsuniversity.player.domain.model.PlayerId;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Valida el token de cada petición protegida y devuelve su jugador. */
@Service
public class AuthenticateService implements AuthenticateUseCase {

    private final SessionRepository sessions;
    private final TokenService tokens;
    private final Clock clock;

    public AuthenticateService(SessionRepository sessions, TokenService tokens, Clock clock) {
        this.sessions = sessions;
        this.tokens = tokens;
        this.clock = clock;
    }

    @Override
    @Transactional
    public PlayerId authenticate(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthenticatedException();
        }
        String hash = tokens.hash(token);
        AuthSession session = sessions.findByTokenHash(hash).orElseThrow(UnauthenticatedException::new);
        if (session.isExpired(clock.instant())) {
            sessions.deleteByTokenHash(hash);
            throw new UnauthenticatedException();
        }
        return session.playerId();
    }
}
