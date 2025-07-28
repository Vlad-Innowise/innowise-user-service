--liquibase formatted sql

--changeset Vlad:20250708_004_1849

ALTER TABLE app.users
ADD COLUMN created_at TIMESTAMP(3);

ALTER TABLE app.users
ADD COLUMN updated_at TIMESTAMP(3);

-- filling in new columns with timestamps
UPDATE app.users
SET created_at = now(), updated_at = now()
WHERE created_at IS NULL OR updated_at IS NULL;

ALTER TABLE app.users
ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE app.users
ALTER COLUMN updated_at SET NOT NULL;