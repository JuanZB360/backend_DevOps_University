package com.devopsuniversity.shared.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Orígenes autorizados a llamar a la API desde el navegador.
 * Se configura con {@code app.cors.allowed-origins} (o la variable de entorno APP_CORS_ALLOWED_ORIGINS).
 */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(List<String> allowedOrigins) {

    public CorsProperties {
        allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
    }
}
