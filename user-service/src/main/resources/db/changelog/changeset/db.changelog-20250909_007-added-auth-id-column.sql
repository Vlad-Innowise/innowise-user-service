--liquibase formatted sql

--changeset Vlad:20250909_007_1710

ALTER TABLE app.users
ADD COLUMN auth_id BIGINT;

ALTER TABLE app.users
ADD CONSTRAINT users_auth_id_unq UNIQUE (auth_id);