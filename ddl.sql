SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER SCHEMA PUBLIC OWNER TO rinha;

SET default_tablespace = '';

SET default_table_access_method = heap;

DROP TABLE IF EXISTS PUBLIC."PESSOAS";

CREATE TABLE PUBLIC."PESSOAS" (
    "ID" uuid not null,
    "APELIDO" varchar(32) unique not null,
    "NASCIMENTO" varchar(12) not null,
    "NOME" varchar(100) not null,
    "STACK" varchar(255),
    primary key ("ID"),
    ,BUSCA_TRGM TEXT GENERATED ALWAYS AS (
        NOME || ' ' || APELIDO || ' ' || COALESCE(STACK, '')
    ) STORED not null
);


CREATE INDEX CONCURRENTLY IF NOT EXISTS IDX_PESSOAS_BUSCA_TGRM ON PESSOA USING GIST (BUSCA_TRGM GIST_TRGM_OPS(siglen=256)) 
INCLUDE(apelido, nascimento, nome, publicID, stack);

ALTER TABLE PUBLIC."PESSOAS" OWNER TO rinha;
