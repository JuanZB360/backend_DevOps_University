package com.devopsuniversity.auth.application.service;

import com.devopsuniversity.auth.application.port.in.AuthResult;
import com.devopsuniversity.auth.application.port.in.RegisterAccountUseCase;
import com.devopsuniversity.auth.domain.exception.UsernameTakenException;
import com.devopsuniversity.auth.domain.model.Account;
import com.devopsuniversity.auth.domain.model.RawPassword;
import com.devopsuniversity.auth.domain.model.Username;
import com.devopsuniversity.auth.domain.port.AccountRepository;
import com.devopsuniversity.auth.domain.port.PasswordHasher;
import com.devopsuniversity.player.application.port.in.RegisterPlayerUseCase;
import com.devopsuniversity.player.domain.model.Player;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Alta de cuentas: crea el jugador (módulo player), su cuenta y una sesión,
 * todo en la misma transacción (o se crea todo o nada).
 */
@Service
public class RegisterAccountService implements RegisterAccountUseCase {

    private final AccountRepository accounts;
    private final PasswordHasher hasher;
    private final RegisterPlayerUseCase registerPlayer;
    private final SessionIssuer sessions;
    private final Clock clock;

    public RegisterAccountService(AccountRepository accounts, PasswordHasher hasher,
                                  RegisterPlayerUseCase registerPlayer, SessionIssuer sessions, Clock clock) {
        this.accounts = accounts;
        this.hasher = hasher;
        this.registerPlayer = registerPlayer;
        this.sessions = sessions;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AuthResult register(Command command) {
        Username username = Username.of(command.username());
        RawPassword password = new RawPassword(command.password());
        if (accounts.existsByUsername(username)) {
            throw new UsernameTakenException(username);
        }
        // El nombre visible del jugador es su propio usuario.
        Player player = registerPlayer.register(new RegisterPlayerUseCase.Command(username.value()));
        accounts.save(new Account(player.getId(), username, hasher.hash(password), clock.instant()));
        return sessions.openFor(player);
    }
}
