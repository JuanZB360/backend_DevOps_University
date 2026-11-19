#!/usr/bin/env bash
# Crea en el PostgreSQL local el usuario y la base de datos que usa el backend.
#
#   ./scripts/create-local-database.sh
#
# Necesita permisos de administrador de PostgreSQL (se ejecuta como el usuario
# del sistema "postgres" con sudo). Es seguro ejecutarlo varias veces: si el
# usuario o la base ya existen, no los toca.
#
# Las tablas NO se crean aquí: las crea Flyway al arrancar el backend
# (src/main/resources/db/migration).
#
# Toma DB_NAME, DB_USERNAME y DB_PASSWORD de backend/.env (los mismos que usa
# el backend). Si no existe, cópialo antes:  cp .env.example .env
set -euo pipefail

ENV_FILE="$(dirname "$0")/../.env"
if [[ -f "$ENV_FILE" ]]; then
  set -a; source "$ENV_FILE"; set +a
fi

DB_NAME="${DB_NAME:-devopsu}"
DB_USERNAME="${DB_USERNAME:-devopsu}"
DB_PASSWORD="${DB_PASSWORD:?Falta DB_PASSWORD: crea backend/.env (cp .env.example .env)}"

run_sql() {
  sudo -u postgres psql --no-psqlrc -v ON_ERROR_STOP=1 -qtA "$@"
}

if [[ "$(run_sql -c "SELECT 1 FROM pg_roles WHERE rolname = '${DB_USERNAME}'")" == "1" ]]; then
  echo "• El usuario '${DB_USERNAME}' ya existe."
else
  run_sql -c "CREATE ROLE \"${DB_USERNAME}\" LOGIN PASSWORD '${DB_PASSWORD}';"
  echo "✓ Usuario '${DB_USERNAME}' creado."
fi

if [[ "$(run_sql -c "SELECT 1 FROM pg_database WHERE datname = '${DB_NAME}'")" == "1" ]]; then
  echo "• La base de datos '${DB_NAME}' ya existe."
else
  run_sql -c "CREATE DATABASE \"${DB_NAME}\" OWNER \"${DB_USERNAME}\";"
  echo "✓ Base de datos '${DB_NAME}' creada."
fi

echo
echo "Listo. Arranca el backend con:  ./mvnw spring-boot:run"
