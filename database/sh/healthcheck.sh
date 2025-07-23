#!/bin/bash

set -e

: "${AUTH_DB:=auth}"
: "${AUTH_DB_USER:=auth}"
: "${AUTH_DB_PASS:=auth_pass}"

: "${MAIN_DB:=main}"
: "${MAIN_DB_USER:=main}"
: "${MAIN_DB_PASS:=main_pass}"

: "${ADMIN_USERNAME:=admin}"
: "${ADMIN_PASSWORD:?ADMIN_PASSWORD is required}"
NEW_ADMIN_UUID=${NEW_ADMIN_UUID:-$(cat /proc/sys/kernel/random/uuid)}

psql -v ON_ERROR_STOP=1 --username "$AUTH_DB_USER" --dbname "$AUTH_DB" <<-EOSQL
    CREATE EXTENSION IF NOT EXISTS "pgcrypto";
    INSERT INTO users (username, password, role, uuid)
    VALUES (
      '$ADMIN_USERNAME',
      crypt('$ADMIN_PASSWORD', gen_salt('bf')),
      'admin',
      '$NEW_ADMIN_UUID'
    )
    ON CONFLICT (username) DO NOTHING;
EOSQL
psql -v ON_ERROR_STOP=1 --username "$MAIN_DB_USER" --dbname "$MAIN_DB" <<-EOSQL
    INSERT INTO users (username, role, uuid)
    VALUES (
      '$ADMIN_USERNAME',
      'admin',
      '$NEW_ADMIN_UUID'
    )
    ON CONFLICT (username) DO NOTHING;
EOSQL

pg_isready -U "${POSTGRES_USER:-postgres}" -d "${POSTGRES_DB:-postgres}"
exit $?
