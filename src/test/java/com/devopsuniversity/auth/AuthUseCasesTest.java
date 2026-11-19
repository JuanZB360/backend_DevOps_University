package com.devopsuniversity.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.devopsuniversity.auth.application.port.in.AuthResult;
import com.devopsuniversity.auth.application.port.in.LoginUseCase;
import com.devopsuniversity.auth.application.port.in.RegisterAccountUseCase;
import com.devopsuniversity.auth.application.service.AuthenticateService;
import com.devopsuniversity.auth.application.service.LoginService;
import com.devopsuniversity.auth.application.service.LogoutService;
import com.devopsuniversity.auth.application.service.RegisterAccountService;
import com.devopsuniversity.auth.application.service.SessionIssuer;
import com.devopsuniversity.auth.application.service.SessionPolicy;
import com.devopsuniversity.auth.domain.exception.InvalidCredentialsException;
import com.devopsuniversity.auth.domain.exception.InvalidCredentialsFormatException;
import com.devopsuniversity.auth.domain.exception.UnauthenticatedException;
import com.devopsuniversity.auth.domain.exception.UsernameTakenException;
import com.devopsuniversity.auth.domain.model.Account;
import com.devopsuniversity.auth.domain.model.AuthSession;
import com.devopsuniversity.auth.domain.model.RawPassword;
import com.devopsuniversity.auth.domain.model.Username;
import com.devopsuniversity.auth.domain.port.AccountRepository;
import com.devopsuniversity.auth.domain.port.PasswordHasher;
import com.devopsuniversity.auth.domain.port.SessionRepository;
import com.devopsuniversity.auth.domain.port.TokenService;
import com.devopsuniversity.player.application.service.GetPlayerService;
import com.devopsuniversity.player.application.service.RegisterPlayerService;
import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerId;
import com.devopsuniversity.player.domain.port.PlayerRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * Tests de los casos de uso de autenticación con dobles en memoria:
 * sin base de datos, sin BCrypt real y con un reloj que se puede adelantar.
 */
class AuthUseCasesTest {

    /** Reloj manipulable para probar la caducidad de sesiones. */
    private Instant now = Instant.parse("2026-11-15T10:00:00Z");
    private final Clock clock = new Clock() {
        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }
    };

    private final Map<PlayerId, Player> players = new HashMap<>();
    private final PlayerRepository playerRepository = new PlayerRepository() {
        @Override
        public Player save(Player player) {
            players.put(player.getId(), player);
            return player;
        }

        @Override
        public Optional<Player> findById(PlayerId id) {
            return Optional.ofNullable(players.get(id));
        }

        @Override
        public List<Player> findTopByXp(int limit) {
            return List.of();
        }
    };

    private final Map<String, Account> accounts = new HashMap<>();
    private final AccountRepository accountRepository = new AccountRepository() {
        @Override
        public Account save(Account account) {
            accounts.put(account.username().value(), account);
            return account;
        }

        @Override
        public Optional<Account> findByUsername(Username username) {
            return Optional.ofNullable(accounts.get(username.value()));
        }

        @Override
        public boolean existsByUsername(Username username) {
            return accounts.containsKey(username.value());
        }
    };

    private final Map<String, AuthSession> sessions = new HashMap<>();
    private final SessionRepository sessionRepository = new SessionRepository() {
        @Override
        public AuthSession save(AuthSession session) {
            sessions.put(session.tokenHash(), session);
            return session;
        }

        @Override
        public Optional<AuthSession> findByTokenHash(String tokenHash) {
            return Optional.ofNullable(sessions.get(tokenHash));
        }

        @Override
        public void deleteByTokenHash(String tokenHash) {
            sessions.remove(tokenHash);
        }
    };

    /** "Hash" reversible: suficiente para probar la lógica, nunca para producción. */
    private final PasswordHasher hasher = new PasswordHasher() {
        @Override
        public String hash(RawPassword password) {
            return "hashed:" + password.value();
        }

        @Override
        public boolean matches(String rawPassword, String hash) {
            return hash.equals("hashed:" + rawPassword);
        }
    };

    private final AtomicInteger tokenCounter = new AtomicInteger();
    private final TokenService tokens = new TokenService() {
        @Override
        public String generate() {
            return "token-" + tokenCounter.incrementAndGet();
        }

        @Override
        public String hash(String token) {
            return "sha:" + token;
        }
    };

    private final SessionIssuer issuer = new SessionIssuer(sessionRepository, tokens, new SessionPolicy(Duration.ofDays(30)), clock);
    private final RegisterAccountService register = new RegisterAccountService(
            accountRepository, hasher, new RegisterPlayerService(playerRepository, clock), issuer, clock);
    private final LoginService login = new LoginService(accountRepository, hasher, new GetPlayerService(playerRepository), issuer);
    private final LogoutService logout = new LogoutService(sessionRepository, tokens);
    private final AuthenticateService authenticate = new AuthenticateService(sessionRepository, tokens, clock);

    private AuthResult registerNeo() {
        return register.register(new RegisterAccountUseCase.Command("Neo", "matrix1999"));
    }

    @Test
    void registeringCreatesPlayerAccountAndSession() {
        AuthResult result = registerNeo();

        assertThat(result.player().getName().username()).isEqualTo("neo");
        assertThat(accounts.get("neo").passwordHash()).isEqualTo("hashed:matrix1999");
        assertThat(sessions).containsKey("sha:" + result.token());
        assertThat(authenticate.authenticate(result.token())).isEqualTo(result.player().getId());
    }

    @Test
    void rejectsDuplicateUsernames() {
        registerNeo();
        assertThatThrownBy(this::registerNeo).isInstanceOf(UsernameTakenException.class);
    }

    @Test
    void loginChecksThePassword() {
        PlayerId id = registerNeo().player().getId();

        assertThat(login.login(new LoginUseCase.Command("neo", "matrix1999")).player().getId()).isEqualTo(id);
        assertThatThrownBy(() -> login.login(new LoginUseCase.Command("neo", "otra-clave1")))
                .isInstanceOf(InvalidCredentialsException.class);
        assertThatThrownBy(() -> login.login(new LoginUseCase.Command("smith", "matrix1999")))
                .isInstanceOf(InvalidCredentialsException.class);
        // Un usuario con formato imposible es simplemente "incorrecto", no un 400 que revele las reglas.
        assertThatThrownBy(() -> login.login(new LoginUseCase.Command("!!", "matrix1999")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void logoutInvalidatesOnlyThatSession() {
        AuthResult first = registerNeo();
        AuthResult second = login.login(new LoginUseCase.Command("neo", "matrix1999"));

        logout.logout(first.token());

        assertThatThrownBy(() -> authenticate.authenticate(first.token())).isInstanceOf(UnauthenticatedException.class);
        assertThat(authenticate.authenticate(second.token())).isEqualTo(second.player().getId());
    }

    @Test
    void expiredSessionsAreRejectedAndRemoved() {
        AuthResult result = registerNeo();
        now = now.plus(Duration.ofDays(31));

        assertThatThrownBy(() -> authenticate.authenticate(result.token())).isInstanceOf(UnauthenticatedException.class);
        assertThat(sessions).isEmpty();
    }

    @Test
    void validatesCredentialFormat() {
        assertThat(Username.of("  Juan_Dev ").value()).isEqualTo("juan_dev");
        assertThatThrownBy(() -> Username.of("root")).isInstanceOf(InvalidCredentialsFormatException.class);
        assertThatThrownBy(() -> Username.of("9lives")).isInstanceOf(InvalidCredentialsFormatException.class);
        assertThatThrownBy(() -> Username.of("ab")).isInstanceOf(InvalidCredentialsFormatException.class);
        assertThatThrownBy(() -> new RawPassword("soloLetras")).isInstanceOf(InvalidCredentialsFormatException.class);
        assertThatThrownBy(() -> new RawPassword("12345678")).isInstanceOf(InvalidCredentialsFormatException.class);
        assertThat(new RawPassword("secreta123")).hasToString("********");
    }
}
