package com.devopsuniversity.player.domain.port;

import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerId;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: lo que el dominio necesita de la persistencia.
 *
 * <p>El dominio define la interfaz y la infraestructura la implementa
 * (Inversión de Dependencias). Cambiar de PostgreSQL a otra base de datos
 * solo requiere un adaptador nuevo.</p>
 */
public interface PlayerRepository {

    Player save(Player player);

    Optional<Player> findById(PlayerId id);

    /** Jugadores con más experiencia, de mayor a menor. */
    List<Player> findTopByXp(int limit);
}
