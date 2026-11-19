package com.devopsuniversity.player.application.port.in;

import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerId;

/** Caso de uso: consultar un jugador por su id. */
public interface GetPlayerUseCase {

    /** @throws com.devopsuniversity.player.domain.exception.PlayerNotFoundException si no existe */
    Player get(PlayerId id);
}
