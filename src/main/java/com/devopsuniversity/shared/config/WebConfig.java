package com.devopsuniversity.shared.config;

import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configuración transversal: CORS y reloj del sistema. */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties cors;

    public WebConfig(CorsProperties cors) {
        this.cors = cors;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Patrones: admite "http://localhost:[*]" (cualquier puerto) además de orígenes exactos.
                .allowedOriginPatterns(cors.allowedOrigins().toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    /** Reloj inyectable: en los tests se sustituye por uno fijo para resultados deterministas. */
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
