package com.devopsuniversity.player.domain.exception;

import com.devopsuniversity.player.domain.model.PlayerId;
import com.devopsuniversity.shared.domain.DomainException;

/** No existe ningún jugador con el id indicado. */
public class PlayerNotFoundException extends DomainException {

    public PlayerNotFoundException(PlayerId id) {
        super("PLAYER_NOT_FOUND", "No existe el jugador " + id);
    }
}
