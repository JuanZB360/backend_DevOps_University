package com.devopsuniversity.player.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.devopsuniversity.player.application.port.in.RegisterPlayerUseCase;
import com.devopsuniversity.player.application.port.in.SyncProgressUseCase;
import com.devopsuniversity.player.application.service.GetPlayerService;
import com.devopsuniversity.player.application.service.LeaderboardService;
import com.devopsuniversity.player.application.service.RegisterPlayerService;
import com.devopsuniversity.player.application.service.SyncProgressService;
import com.devopsuniversity.player.domain.exception.PlayerNotFoundException;
import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

/** Tests de los casos de uso con un repositorio en memoria y un reloj fijo. */
class PlayerUseCasesTest {

    private final InMemoryPlayerRepository repository = new InMemoryPlayerRepository();
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-11T10:00:00Z"), ZoneOffset.UTC);

    private final RegisterPlayerService register = new RegisterPlayerService(repository, clock);
    private final GetPlayerService get = new GetPlayerService(repository);
    private final SyncProgressService sync = new SyncProgressService(repository, clock);
    private final LeaderboardService leaderboard = new LeaderboardService(repository);

    private Player registerWithXp(String name, int xp) {
        Player player = register.register(new RegisterPlayerUseCase.Command(name));
        return sync.sync(new SyncProgressUseCase.Command(player.getId(), xp, 1, 0, 0, null, false));
    }

    @Test
    void registersAndRetrievesPlayer() {
        Player created = register.register(new RegisterPlayerUseCase.Command("Trinity"));
        assertThat(get.get(created.getId()).getName().username()).isEqualTo("trinity");
        assertThat(created.getCreatedAt()).isEqualTo(clock.instant());
    }

    @Test
    void failsForUnknownPlayer() {
        assertThatThrownBy(() -> get.get(PlayerId.newId())).isInstanceOf(PlayerNotFoundException.class);
    }

    @Test
    void syncsProgressAndKeepsSnapshot() {
        Player player = register.register(new RegisterPlayerUseCase.Command("Neo"));
        Player synced = sync.sync(new SyncProgressUseCase.Command(player.getId(), 250, 3, 45, 6, "{\"version\":1}", false));
        assertThat(synced.getProgress().level()).isEqualTo(3);
        assertThat(synced.getProgress().saveSnapshot()).isEqualTo("{\"version\":1}");
    }

    @Test
    void leaderboardIsSortedAndLimited() {
        registerWithXp("Neo", 100);
        registerWithXp("Trinity", 500);
        registerWithXp("Morpheus", 300);

        assertThat(leaderboard.top(2)).extracting(p -> p.getName().displayName()).containsExactly("Trinity", "Morpheus");
        assertThat(leaderboard.top(0)).hasSize(1); // el límite se acota a un mínimo de 1
    }

    @Test
    void replaceAllowsGoingBackToZero() {
        Player player = registerWithXp("Morpheus", 900);
        Player reset = sync.sync(new SyncProgressUseCase.Command(player.getId(), 0, 1, 20, 0, "{}", true));
        assertThat(reset.getProgress().xp()).isZero();
    }
}
