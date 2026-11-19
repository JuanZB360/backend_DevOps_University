package com.devopsuniversity.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio generado por Spring Data. Solo lo usa {@link AccountPersistenceAdapter}. */
interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, UUID> {

    Optional<AccountJpaEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
