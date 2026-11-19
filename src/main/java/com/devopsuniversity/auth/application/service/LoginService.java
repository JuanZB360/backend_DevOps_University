package com.devopsuniversity.auth.application.service;

import com.devopsuniversity.auth.application.port.in.AuthResult;
import com.devopsuniversity.auth.application.port.in.LoginUseCase;
import com.devopsuniversity.auth.domain.exception.InvalidCredentialsException;
import com.devopsuniversity.auth.domain.exception.InvalidCredentialsFormatException;
import com.devopsuniversity.auth.domain.model.Account;
import com.devopsuniversity.auth.domain.model.RawPassword;
import com.devopsuniversity.auth.domain.model.Username;
import com.devopsuniversity.auth.domain.port.AccountRepository;
import com.devopsuniversity.auth.domain.port.PasswordHasher;
import com.devopsuniversity.player.application.port.in.GetPlayerUseCase;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Inicio de sesión con usuario y contraseña. */
@Service
public class LoginService implements LoginUseCase {

    private final AccountRepository accounts;
    private final PasswordHasher hasher;
    private final GetPlayerUseCase getPlayer;
    private final SessionIssuer sessions;
    /**
     * Hash de una contraseña cualquiera. Si el usuario no existe se compara
     * contra él igualmente, para que la respuesta tarde lo mismo y no se pueda
     * deducir qué usuarios existen midiendo tiempos.
     */
    private final String dummyHash;

    public LoginService(AccountRepository accounts, PasswordHasher hasher,
                        GetPlayerUseCase getPlayer, SessionIssuer sessions) {
        this.accounts = accounts;
        this.hasher = hasher;
        this.getPlayer = getPlayer;
        this.sessions = sessions;
        this.dummyHash = hasher.hash(new RawPassword("usuario-inexistente-0"));
    }

    @Override
    @Transactional
    public AuthResult login(Command command) {
        String password = command.password() == null ? "" : command.password();
        Optional<Account> found = findAccount(command.username());
        if (found.isEmpty()) {
            hasher.matches(password, dummyHash); // solo para igualar el tiempo de respuesta
            throw new InvalidCredentialsException();
        }
        Account account = found.get();
        if (!hasher.matches(password, account.passwordHash())) {
            throw new InvalidCredentialsException();
        }
        return sessions.openFor(getPlayer.get(account.playerId()));
    }

    /** Un usuario con formato inválido no puede existir: se trata como "no encontrado". */
    private Optional<Account> findAccount(String rawUsername) {
        try {
            return accounts.findByUsername(Username.of(rawUsername));
        } catch (InvalidCredentialsFormatException e) {
            return Optional.empty();
        }
    }
}
