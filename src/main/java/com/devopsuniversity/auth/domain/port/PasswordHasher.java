package com.devopsuniversity.auth.domain.port;

import com.devopsuniversity.auth.domain.model.RawPassword;

/**
 * Puerto para el algoritmo de hash de contraseñas.
 *
 * <p>El dominio no sabe que se usa BCrypt: si mañana se cambia a Argon2 solo
 * se toca el adaptador de infraestructura.</p>
 */
public interface PasswordHasher {

    String hash(RawPassword password);

    /** Compara en tiempo constante una contraseña con un hash guardado. */
    boolean matches(String rawPassword, String hash);
}
