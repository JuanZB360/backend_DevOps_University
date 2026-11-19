package com.devopsuniversity.auth.infrastructure.persistence;

import com.devopsuniversity.auth.domain.model.Account;
import com.devopsuniversity.auth.domain.model.Username;
import com.devopsuniversity.auth.domain.port.AccountRepository;
import com.devopsuniversity.player.domain.model.PlayerId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de salida: implementa {@link AccountRepository} con Spring Data JPA. */
@Component
class AccountPersistenceAdapter implements AccountRepository {

    private final SpringDataAccountRepository jpa;

    AccountPersistenceAdapter(SpringDataAccountRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Account save(Account account) {
        jpa.save(new AccountJpaEntity(account.playerId().value(), account.username().value(),
                account.passwordHash(), account.createdAt()));
        return account;
    }

    @Override
    public Optional<Account> findByUsername(Username username) {
        return jpa.findByUsername(username.value()).map(AccountPersistenceAdapter::toDomain);
    }

    @Override
    public boolean existsByUsername(Username username) {
        return jpa.existsByUsername(username.value());
    }

    private static Account toDomain(AccountJpaEntity entity) {
        return new Account(new PlayerId(entity.getPlayerId()), new Username(entity.getUsername()),
                entity.getPasswordHash(), entity.getCreatedAt());
    }
}
