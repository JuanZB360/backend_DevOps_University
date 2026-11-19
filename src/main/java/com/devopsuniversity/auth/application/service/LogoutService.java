package com.devopsuniversity.auth.application.service;

import com.devopsuniversity.auth.application.port.in.LogoutUseCase;
import com.devopsuniversity.auth.domain.port.SessionRepository;
import com.devopsuniversity.auth.domain.port.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cierre de sesión: borra la sesión del servidor (el token deja de servir). */
@Service
public class LogoutService implements LogoutUseCase {

    private final SessionRepository sessions;
    private final TokenService tokens;

    public LogoutService(SessionRepository sessions, TokenService tokens) {
        this.sessions = sessions;
        this.tokens = tokens;
    }

    @Override
    @Transactional
    public void logout(String token) {
        if (token != null && !token.isBlank()) {
            sessions.deleteByTokenHash(tokens.hash(token));
        }
    }
}
