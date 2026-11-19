package com.devopsuniversity.player.domain.model;

import com.devopsuniversity.player.domain.exception.InvalidPlayerNameException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;

/**
 * Nombre del jugador y su usuario Linux derivado (Value Object).
 *
 * <p>Las reglas son las mismas que en el frontend
 * ({@code frontend/src/game/session/newGame.ts}): si cambias una, cambia la otra.</p>
 *
 * @param displayName nombre tal como lo escribió el jugador (ej. "José Pérez")
 * @param username    usuario del servidor simulado (ej. "joseperez")
 */
public record PlayerName(String displayName, String username) {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 24;
    private static final int MAX_USERNAME = 16;
    private static final Set<String> RESERVED_USERS = Set.of("root", "daemon", "www-data", "nobody", "admin");

    /**
     * Valida el nombre y calcula el usuario.
     *
     * @throws InvalidPlayerNameException si el nombre no cumple las reglas
     */
    public static PlayerName of(String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
            throw new InvalidPlayerNameException("El nombre debe tener entre %d y %d caracteres".formatted(MIN_LENGTH, MAX_LENGTH));
        }
        if (!name.matches(".*[a-zA-Z].*")) {
            throw new InvalidPlayerNameException("El nombre debe contener al menos una letra");
        }
        return new PlayerName(name, toUsername(name));
    }

    /** "José Pérez" → "joseperez". Debe empezar por letra y no coincidir con usuarios del sistema. */
    static String toUsername(String displayName) {
        String username = Normalizer.normalize(displayName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]", "")
                .replaceAll("^[^a-z]+", "");
        if (username.length() > MAX_USERNAME) {
            username = username.substring(0, MAX_USERNAME);
        }
        if (username.isEmpty()) {
            username = "neo";
        }
        return RESERVED_USERS.contains(username) ? username + "1" : username;
    }
}
