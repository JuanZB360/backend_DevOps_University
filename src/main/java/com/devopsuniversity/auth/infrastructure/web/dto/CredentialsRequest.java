package com.devopsuniversity.auth.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo de {@code POST /api/v1/auth/register} y {@code POST /api/v1/auth/login}.
 * Las reglas de formato las aplica el dominio ({@code Username}, {@code RawPassword}).
 */
public record CredentialsRequest(
        @NotBlank(message = "El usuario es obligatorio") String username,
        @NotBlank(message = "La contraseña es obligatoria") String password) {

    /** Nunca imprimir la contraseña (por ejemplo en logs de depuración). */
    @Override
    public String toString() {
        return "CredentialsRequest[username=" + username + ", password=********]";
    }
}
