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

psql -v ON_ERROR_STOP=1 --username "$AUTH_DB_USER" --dbname "$AUTH_DB" <<-EOSQL
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS "pgcrypto";

    CREATE TABLE IF NOT EXISTS users
    (
        uid BIGSERIAL PRIMARY KEY,
        uuid uuid DEFAULT uuid_generate_v4() NOT NULL CONSTRAINT uni_users_uuid UNIQUE,
        username TEXT NOT NULL CONSTRAINT uni_users_username UNIQUE,
        password TEXT NOT NULL,
        ROLE TEXT DEFAULT 'user'::TEXT NOT NULL
    );
EOSQL

psql -v ON_ERROR_STOP=1 --username "$MAIN_DB_USER" --dbname "$MAIN_DB" <<-EOSQL
    CREATE TABLE users
    (
        id       BIGINT       GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        role     VARCHAR(255),
        username VARCHAR(255) NOT NULL CONSTRAINT username_pk UNIQUE,
        uuid     uuid         NOT NULL CONSTRAINT uuid_pk UNIQUE
    );
    ALTER TABLE users OWNER TO main;

    CREATE TABLE seats
    (
        id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        col INTEGER NOT NULL CONSTRAINT seats_col_check CHECK (col >= 1),
        row INTEGER NOT NULL CONSTRAINT seats_row_check CHECK ("row" >= 1),
        CONSTRAINT row_col_pk UNIQUE (row, col)
    );
    ALTER TABLE seats OWNER TO main;


    CREATE TABLE reservations
    (
        id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        end_time   TIMESTAMP(6) NOT NULL,
        start_time TIMESTAMP(6) NOT NULL,
        seat_id    BIGINT       NOT NULL CONSTRAINT reservations_seats_fk REFERENCES seats,
        user_id    BIGINT       NOT NULL CONSTRAINT reservation_users_fk  REFERENCES users
    );
    ALTER TABLE reservations
        ADD CONSTRAINT start_before_end
            CHECK (start_time < end_time);
    CREATE EXTENSION IF NOT EXISTS btree_gist;
    ALTER TABLE reservations
        ADD CONSTRAINT no_overlapping_reservations
            EXCLUDE USING gist
            (
                seat_id                         WITH =,
                tsrange(start_time, end_time)   WITH &&
            );

    ALTER TABLE reservations OWNER TO main;
EOSQL
