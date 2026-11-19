-- Cuentas de acceso (usuario + contraseña) y sesiones abiertas.
-- Compatible con PostgreSQL (producción) y H2 en modo PostgreSQL (tests).

-- Una cuenta por jugador. Solo se guarda el hash BCrypt de la contraseña.
CREATE TABLE accounts (
    player_id     UUID                     PRIMARY KEY REFERENCES players (id) ON DELETE CASCADE,
    username      VARCHAR(16)              NOT NULL,
    password_hash VARCHAR(100)             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_accounts_username UNIQUE (username)
);

-- Sesiones: se guarda el SHA-256 del token, nunca el token en claro.
-- Cerrar sesión = borrar la fila.
CREATE TABLE auth_sessions (
    token_hash VARCHAR(64)              PRIMARY KEY,
    player_id  UUID                     NOT NULL REFERENCES players (id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_auth_sessions_player ON auth_sessions (player_id);
