-- Jugadores de DevOps University y su último progreso sincronizado.
-- Compatible con PostgreSQL (producción) y H2 en modo PostgreSQL (desarrollo y tests).
CREATE TABLE players (
    id                 UUID                     PRIMARY KEY,
    display_name       VARCHAR(24)              NOT NULL,
    username           VARCHAR(16)              NOT NULL,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    xp                 INTEGER                  NOT NULL DEFAULT 0,
    level              INTEGER                  NOT NULL DEFAULT 1,
    points             INTEGER                  NOT NULL DEFAULT 0,
    completed_missions INTEGER                  NOT NULL DEFAULT 0,
    -- Partida completa en JSON (para continuar en otro dispositivo)
    save_snapshot      TEXT,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL
);

-- El ranking ordena por experiencia.
CREATE INDEX idx_players_xp ON players (xp DESC);
