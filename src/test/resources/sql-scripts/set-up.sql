CREATE TYPE  status_type AS ENUM('READY', 'ACTIVE');

CREATE CAST (character varying as status_type) WITH INOUT AS IMPLICIT;