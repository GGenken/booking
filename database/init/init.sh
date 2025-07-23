#!/bin/bash

# since .sql files that Docker picks up only execute in $POSTGRES_DB database, have to use this .sh
set -e

# fallback values
: "${AUTH_DB:=auth}"
: "${AUTH_DB_USER:=auth}"
: "${AUTH_DB_PASS:=auth_pass}"

: "${MAIN_DB:=main}"
: "${MAIN_DB_USER:=main}"
: "${MAIN_DB_PASS:=main_pass}"

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE $AUTH_DB;
    CREATE DATABASE $MAIN_DB;

    REVOKE CONNECT ON DATABASE $AUTH_DB FROM PUBLIC;
    REVOKE CONNECT ON DATABASE $MAIN_DB FROM PUBLIC;

    CREATE USER $AUTH_DB_USER WITH PASSWORD '$AUTH_DB_PASS';
    CREATE USER $MAIN_DB_USER WITH PASSWORD '$MAIN_DB_PASS';

    GRANT ALL PRIVILEGES ON DATABASE $AUTH_DB TO $AUTH_DB_USER;
    GRANT ALL PRIVILEGES ON DATABASE $MAIN_DB TO $MAIN_DB_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$AUTH_DB" <<-EOSQL
    ALTER SCHEMA public OWNER TO $AUTH_DB_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$MAIN_DB" <<-EOSQL
    ALTER SCHEMA public OWNER TO $MAIN_DB_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$AUTH_DB_USER" --dbname "$AUTH_DB" < /docker-entrypoint-initdb.d/sql/01-auth.sql
psql -v ON_ERROR_STOP=1 --username "$MAIN_DB_USER" --dbname "$MAIN_DB" < /docker-entrypoint-initdb.d/sql/02-main.sql
