package com.devopsuniversity.auth.domain.model;

import com.devopsuniversity.auth.domain.exception.InvalidCredentialsFormatException;
import java.nio.charset.StandardCharsets;

/**
 * Contraseña en claro recién recibida (Value Object de vida muy corta).
 *
 * <p>Solo existe para validarla y pasarla al {@code PasswordHasher}: nunca se
 * guarda ni se registra en logs ({@link #toString()} la oculta).</p>
 */
public record RawPassword(String value) {

    public static final int MIN_LENGTH = 8;
    /** BCrypt ignora lo que pase de 72 bytes: lo rechazamos para no engañar a nadie. */
    public static final int MAX_BYTES = 72;

    public RawPassword {
        if (value == null || value.length() < MIN_LENGTH) {
            throw new InvalidCredentialsFormatException(
                    "La contraseña debe tener al menos %d caracteres".formatted(MIN_LENGTH));
        }
        if (value.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES) {
            throw new InvalidCredentialsFormatException("La contraseña es demasiado larga (máximo 72 bytes)");
        }
        if (!value.matches(".*[A-Za-z].*") || !value.matches(".*\\d.*")) {
            throw new InvalidCredentialsFormatException("La contraseña debe combinar letras y números");
        }
    }

    @Override
    public String toString() {
        return "********";
    }
}
