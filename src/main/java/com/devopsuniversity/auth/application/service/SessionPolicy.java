package com.devopsuniversity.auth.application.service;

import java.time.Duration;
import java.util.Objects;

/**
 * Reglas de duración de las sesiones. Se construye desde la configuración
 * ({@code app.auth.session-ttl}) para que la aplicación no dependa de Spring.
 *
 * @param ttl tiempo que dura una sesión desde que se inicia
 */
public record SessionPolicy(Duration ttl) {

    public SessionPolicy {
        Objects.requireNonNull(ttl);
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("La duración de la sesión debe ser positiva");
        }
    }
}
