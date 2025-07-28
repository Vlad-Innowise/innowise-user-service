--liquibase formatted sql

--changeset Vlad:20250709_006_1936

ALTER TABLE app.users
ADD COLUMN version BIGINT;

UPDATE app.users
SET version = 0
WHERE version IS NULL;

ALTER TABLE app.users
ALTER COLUMN version SET NOT NULL;


--changeset Vlad:20250709_006_1940

ALTER TABLE app.card_info
ADD COLUMN version BIGINT;

UPDATE app.card_info
SET version = 0
WHERE version IS NULL;

ALTER TABLE app.card_info
ALTER COLUMN version SET NOT NULL;
