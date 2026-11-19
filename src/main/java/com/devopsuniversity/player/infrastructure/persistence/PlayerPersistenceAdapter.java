package com.devopsuniversity.player.infrastructure.persistence;

import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerId;
import com.devopsuniversity.player.domain.port.PlayerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida: implementa el puerto del dominio {@link PlayerRepository}
 * usando Spring Data JPA.
 */
@Component
class PlayerPersistenceAdapter implements PlayerRepository {

    private final SpringDataPlayerRepository jpa;

    PlayerPersistenceAdapter(SpringDataPlayerRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Player save(Player player) {
        return PlayerPersistenceMapper.toDomain(jpa.save(PlayerPersistenceMapper.toEntity(player)));
    }

    @Override
    public Optional<Player> findById(PlayerId id) {
        return jpa.findById(id.value()).map(PlayerPersistenceMapper::toDomain);
    }

    @Override
    public List<Player> findTopByXp(int limit) {
        return jpa.findAllByOrderByXpDescCreatedAtAsc(PageRequest.of(0, limit)).stream()
                .map(PlayerPersistenceMapper::toDomain)
                .toList();
    }
}
