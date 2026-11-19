package com.devopsuniversity.player.application.port.in;

import com.devopsuniversity.player.domain.model.Player;

/** Caso de uso: registrar un jugador nuevo a partir de su nombre. */
public interface RegisterPlayerUseCase {

    /** Datos de entrada del caso de uso. */
    record Command(String displayName) {
    }

    Player register(Command command);
}
