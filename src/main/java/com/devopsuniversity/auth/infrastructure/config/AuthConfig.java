package com.devopsuniversity.auth.infrastructure.config;

import com.devopsuniversity.auth.application.service.SessionPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Traduce la configuración externa a objetos de la capa de aplicación. */
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
class AuthConfig {

    @Bean
    SessionPolicy sessionPolicy(AuthProperties properties) {
        return new SessionPolicy(properties.sessionTtl());
    }
}
