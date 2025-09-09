--liquibase formatted sql

--changeset Vlad:20250909_008_1714

ALTER TABLE app.users
ALTER COLUMN auth_id SET NOT NULL;
