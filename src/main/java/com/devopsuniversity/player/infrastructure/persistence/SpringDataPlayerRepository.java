package com.devopsuniversity.player.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio generado por Spring Data. Solo lo usa el adaptador de persistencia. */
interface SpringDataPlayerRepository extends JpaRepository<PlayerJpaEntity, UUID> {

    List<PlayerJpaEntity> findAllByOrderByXpDescCreatedAtAsc(Pageable pageable);
}
