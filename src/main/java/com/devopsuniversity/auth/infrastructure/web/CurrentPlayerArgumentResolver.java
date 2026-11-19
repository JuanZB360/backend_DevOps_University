package com.devopsuniversity.auth.infrastructure.web;

import com.devopsuniversity.auth.application.port.in.AuthenticateUseCase;
import com.devopsuniversity.player.domain.model.PlayerId;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Resuelve los parámetros {@link CurrentPlayer} validando el token de la petición. */
class CurrentPlayerArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthenticateUseCase authenticate;

    CurrentPlayerArgumentResolver(AuthenticateUseCase authenticate) {
        this.authenticate = authenticate;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentPlayer.class)
                && PlayerId.class.equals(parameter.getParameterType());
    }

    @Override
    public PlayerId resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        return authenticate.authenticate(request == null ? null : BearerToken.from(request));
    }
}
