package com.devopsuniversity.auth.infrastructure.security;

import com.devopsuniversity.auth.domain.model.RawPassword;
import com.devopsuniversity.auth.domain.port.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador de {@link PasswordHasher} con BCrypt (incluye sal aleatoria y es
 * lento a propósito para dificultar ataques de fuerza bruta).
 */
@Component
class BCryptPasswordHasher implements PasswordHasher {

    /** Coste 10 ≈ 70 ms por hash: seguro y sin hacer esperar al jugador. */
    private static final int STRENGTH = 10;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(STRENGTH);

    @Override
    public String hash(RawPassword password) {
        return encoder.encode(password.value());
    }

    @Override
    public boolean matches(String rawPassword, String hash) {
        return encoder.matches(rawPassword, hash);
    }
}
