package com.devopsuniversity.auth.domain.model;

import com.devopsuniversity.auth.domain.exception.InvalidCredentialsFormatException;
import java.util.Locale;
import java.util.Set;

/**
 * Nombre de usuario con el que se inicia sesión (Value Object).
 *
 * <p>Es también el usuario Linux del servidor simulado, así que sigue las
 * mismas reglas que {@code useradd}: minúsculas, empieza por letra y solo
 * admite letras, números, guion y guion bajo. Debe coincidir con
 * {@code frontend/src/auth/credentials.ts}.</p>
 */
public record Username(String value) {

    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 16;
    private static final String PATTERN = "^[a-z][a-z0-9_-]*$";
    /** Usuarios que ya existen en el servidor simulado. */
    private static final Set<String> RESERVED = Set.of("root", "daemon", "www-data", "nobody", "admin");

    public Username {
        if (value == null || value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new InvalidCredentialsFormatException(
                    "El usuario debe tener entre %d y %d caracteres".formatted(MIN_LENGTH, MAX_LENGTH));
        }
        if (!value.matches(PATTERN)) {
            throw new InvalidCredentialsFormatException(
                    "El usuario solo admite minúsculas, números, '-' y '_', y debe empezar por una letra");
        }
        if (RESERVED.contains(value)) {
            throw new InvalidCredentialsFormatException("'%s' es un usuario reservado del sistema".formatted(value));
        }
    }

    /** Normaliza lo que escribe la persona (espacios y mayúsculas) antes de validar. */
    public static Username of(String raw) {
        return new Username(raw == null ? null : raw.trim().toLowerCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return value;
    }
}
