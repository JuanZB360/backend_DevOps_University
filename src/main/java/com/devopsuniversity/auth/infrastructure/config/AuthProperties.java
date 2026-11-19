package com.devopsuniversity.auth.infrastructure.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuración de autenticación ({@code app.auth.*} en application.yml).
 *
 * @param sessionTtl duración de una sesión (por defecto 30 días)
 */
@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(@DefaultValue("30d") Duration sessionTtl) {
}
