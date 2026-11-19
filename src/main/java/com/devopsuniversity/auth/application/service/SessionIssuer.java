package com.devopsuniversity.auth.application.service;

import com.devopsuniversity.auth.application.port.in.AuthResult;
import com.devopsuniversity.auth.domain.model.AuthSession;
import com.devopsuniversity.auth.domain.port.SessionRepository;
import com.devopsuniversity.auth.domain.port.TokenService;
import com.devopsuniversity.player.domain.model.Player;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Abre sesiones nuevas. Lo comparten el registro y el inicio de sesión para
 * que ambos emitan los tokens exactamente igual.
 */
@Component
public class SessionIssuer {

    private final SessionRepository sessions;
    private final TokenService tokens;
    private final SessionPolicy policy;
    private final Clock clock;

    public SessionIssuer(SessionRepository sessions, TokenService tokens, SessionPolicy policy, Clock clock) {
        this.sessions = sessions;
        this.tokens = tokens;
        this.policy = policy;
        this.clock = clock;
    }

    public AuthResult openFor(Player player) {
        String token = tokens.generate();
        Instant now = clock.instant();
        AuthSession session = new AuthSession(tokens.hash(token), player.getId(), now, now.plus(policy.ttl()));
        sessions.save(session);
        return new AuthResult(token, session.expiresAt(), player);
    }
}
