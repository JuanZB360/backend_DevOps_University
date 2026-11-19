package com.devopsuniversity.player.domain.exception;

import com.devopsuniversity.shared.domain.DomainException;

/** Se intentó sincronizar un progreso más antiguo que el guardado. */
public class StaleProgressException extends DomainException {

    public StaleProgressException(int storedXp, int incomingXp) {
        super("STALE_PROGRESS",
                "El progreso recibido (%d XP) es anterior al guardado (%d XP)".formatted(incomingXp, storedXp));
    }
}
