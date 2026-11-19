package com.devopsuniversity.shared.domain;

/**
 * Base de todas las excepciones de negocio.
 *
 * <p>Permite que la capa web traduzca cualquier regla de dominio violada a una
 * respuesta HTTP coherente sin que el dominio conozca HTTP.</p>
 */
public abstract class DomainException extends RuntimeException {

    /** Código estable y legible por máquinas (ej. {@code PLAYER_NOT_FOUND}). */
    private final String code;

    protected DomainException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
