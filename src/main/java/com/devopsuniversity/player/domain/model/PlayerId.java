package com.devopsuniversity.player.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador de un jugador (Value Object).
 *
 * <p>Envolver el UUID en un tipo propio evita confundirlo con otros
 * identificadores y hace las firmas de los métodos autoexplicativas.</p>
 */
public record PlayerId(UUID value) {

    public PlayerId {
        Objects.requireNonNull(value, "El id del jugador no puede ser nulo");
    }

    public static PlayerId newId() {
        return new PlayerId(UUID.randomUUID());
    }

    public static PlayerId of(String value) {
        return new PlayerId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
