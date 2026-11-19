package com.devopsuniversity.player.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.devopsuniversity.player.domain.exception.InvalidPlayerNameException;
import com.devopsuniversity.player.domain.exception.StaleProgressException;
import com.devopsuniversity.player.domain.model.Player;
import com.devopsuniversity.player.domain.model.PlayerName;
import com.devopsuniversity.player.domain.model.Progress;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests del dominio: Java puro, sin Spring. */
class PlayerTest {

    private static final Instant NOW = Instant.parse("2026-11-11T10:00:00Z");

    @Test
    @DisplayName("deriva un usuario Linux válido del nombre (mismas reglas que el frontend)")
    void derivesUsername() {
        assertThat(PlayerName.of("José Pérez").username()).isEqualTo("joseperez");
        assertThat(PlayerName.of("123 Neo!").username()).isEqualTo("neo");
        assertThat(PlayerName.of("root").username()).isEqualTo("root1");
        assertThat(PlayerName.of("  Trinity  ").displayName()).isEqualTo("Trinity");
    }

    @Test
    @DisplayName("rechaza nombres demasiado cortos, largos o sin letras")
    void rejectsInvalidNames() {
        assertThatThrownBy(() -> PlayerName.of("a")).isInstanceOf(InvalidPlayerNameException.class);
        assertThatThrownBy(() -> PlayerName.of("x".repeat(25))).isInstanceOf(InvalidPlayerNameException.class);
        assertThatThrownBy(() -> PlayerName.of("1234")).isInstanceOf(InvalidPlayerNameException.class);
        assertThatThrownBy(() -> PlayerName.of(null)).isInstanceOf(InvalidPlayerNameException.class);
    }

    @Test
    @DisplayName("un jugador nuevo empieza en nivel 1 sin experiencia")
    void startsFromScratch() {
        Player player = Player.register(PlayerName.of("Neo"), NOW);
        assertThat(player.getProgress()).isEqualTo(Progress.initial(NOW));
    }

    @Test
    @DisplayName("acepta progreso nuevo y rechaza uno más antiguo")
    void syncRejectsStaleProgress() {
        Player player = Player.register(PlayerName.of("Neo"), NOW);
        player.syncProgress(new Progress(300, 3, 40, 5, "{}", NOW));
        assertThat(player.getProgress().xp()).isEqualTo(300);

        assertThatThrownBy(() -> player.syncProgress(new Progress(100, 2, 10, 2, null, NOW)))
                .isInstanceOf(StaleProgressException.class)
                .hasMessageContaining("100 XP");
    }

    @Test
    @DisplayName("el progreso no admite valores negativos")
    void progressValidatesValues() {
        assertThatThrownBy(() -> new Progress(-1, 1, 0, 0, null, NOW)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Progress(0, 0, 0, 0, null, NOW)).isInstanceOf(IllegalArgumentException.class);
    }
}
