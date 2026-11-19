package com.devopsuniversity.auth.infrastructure.web;

import com.devopsuniversity.auth.application.port.in.AuthenticateUseCase;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registra {@link CurrentPlayer} para que los controladores puedan pedir el jugador autenticado. */
@Configuration
class AuthWebConfig implements WebMvcConfigurer {

    private final AuthenticateUseCase authenticate;

    AuthWebConfig(AuthenticateUseCase authenticate) {
        this.authenticate = authenticate;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentPlayerArgumentResolver(authenticate));
    }
}
