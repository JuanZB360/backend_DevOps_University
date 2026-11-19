package com.devopsuniversity.auth.infrastructure.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

/** Extrae el token de la cabecera {@code Authorization: Bearer <token>}. */
final class BearerToken {

    private static final String PREFIX = "Bearer ";

    private BearerToken() {
    }

    /** @return el token, o {@code null} si la petición no trae uno */
    static String from(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.regionMatches(true, 0, PREFIX, 0, PREFIX.length())) {
            return null;
        }
        String token = header.substring(PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }
}
