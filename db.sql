\c  inventory;

CREATE TYPE status_type AS ENUM('READY', 'ACTIVE');

CREATE CAST (character varying as status_type) WITH INOUT AS IMPLICIT;

CREATE TABLE IF NOT EXISTS devices (
    id SERIAL PRIMARY KEY,
    pin_code CHAR(7) NOT NULL,
    status status_type NOT NULL DEFAULT 'READY'::status_type,
    temperature int NOT NULL DEFAULT -1,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(pin_code)
);

