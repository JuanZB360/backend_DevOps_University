package com.devopsuniversity.player.domain.exception;

import com.devopsuniversity.shared.domain.DomainException;

/** El nombre del jugador no cumple las reglas de validación. */
public class InvalidPlayerNameException extends DomainException {

    public InvalidPlayerNameException(String message) {
        super("INVALID_PLAYER_NAME", message);
    }
}
