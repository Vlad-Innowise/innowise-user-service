--liquibase formatted sql

--changeset Vlad:20250629_001_1300

CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 INCREMENT BY 1;

--changeset Vlad:20250629_001_1305

CREATE TABLE app.users
(
id BIGSERIAL,
name VARCHAR(128) NOT NULL,
surname VARCHAR(128) NOT NULL,
birth_date DATE NOT NULL,
email VARCHAR(255) NOT NULL,
CONSTRAINT users_pk PRIMARY KEY (id),
CONSTRAINT users_email_unq UNIQUE(email)
);

ALTER TABLE IF EXISTS app.users
    OWNER to postgres;

