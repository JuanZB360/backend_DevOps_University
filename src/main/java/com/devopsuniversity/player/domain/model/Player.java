package com.devopsuniversity.player.domain.model;

import com.devopsuniversity.player.domain.exception.StaleProgressException;
import java.time.Instant;
import java.util.Objects;

/**
 * Jugador de DevOps University (raíz de agregado).
 *
 * <p>Es Java puro: no conoce Spring, JPA ni HTTP. Toda regla de negocio sobre
 * el jugador vive aquí, de modo que se puede testear sin levantar nada.</p>
 */
public final class Player {

    private final PlayerId id;
    private final PlayerName name;
    private final Instant createdAt;
    private Progress progress;

    private Player(PlayerId id, PlayerName name, Instant createdAt, Progress progress) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.progress = Objects.requireNonNull(progress);
    }

    /** Registra un jugador nuevo. */
    public static Player register(PlayerName name, Instant now) {
        return new Player(PlayerId.newId(), name, now, Progress.initial(now));
    }

    /** Reconstruye un jugador existente (lo usa la capa de persistencia). */
    public static Player restore(PlayerId id, PlayerName name, Instant createdAt, Progress progress) {
        return new Player(id, name, createdAt, progress);
    }

    /**
     * Sincroniza el progreso enviado por el cliente.
     *
     * <p>Regla: no se acepta un progreso con MENOS experiencia que el guardado,
     * para que una pestaña antigua o un dispositivo desactualizado no borre
     * el avance real del jugador.</p>
     *
     * @throws StaleProgressException si el progreso recibido es anterior al guardado
     */
    public void syncProgress(Progress incoming) {
        if (incoming.xp() < progress.xp()) {
            throw new StaleProgressException(progress.xp(), incoming.xp());
        }
        this.progress = incoming;
    }

    /**
     * Sustituye el progreso sin la regla de "no retroceder".
     *
     * <p>Solo para acciones explícitas del jugador: reiniciar la partida desde
     * cero o importar una copia de seguridad.</p>
     */
    public void replaceProgress(Progress incoming) {
        this.progress = incoming;
    }

    public PlayerId getId() {
        return id;
    }

    public PlayerName getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Progress getProgress() {
        return progress;
    }
}
