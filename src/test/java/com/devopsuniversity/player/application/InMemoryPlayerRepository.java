package com.devopsuniversity.player.application;

import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerId;
import com.devopsuniversity.player.domain.port.PlayerRepository;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación falsa del puerto para tests de casos de uso.
 * Demuestra el beneficio de la Inversión de Dependencias: no hace falta base de datos.
 */
class InMemoryPlayerRepository implements PlayerRepository {

    private final Map<PlayerId, Player> store = new HashMap<>();

    @Override
    public Player save(Player player) {
        store.put(player.getId(), player);
        return player;
    }

    @Override
    public Optional<Player> findById(PlayerId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Player> findTopByXp(int limit) {
        return store.values().stream()
                .sorted(Comparator.comparingInt((Player p) -> p.getProgress().xp()).reversed())
                .limit(limit)
                .toList();
    }
}
