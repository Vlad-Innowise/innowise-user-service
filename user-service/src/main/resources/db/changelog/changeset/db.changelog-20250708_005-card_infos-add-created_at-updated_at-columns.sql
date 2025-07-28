--liquibase formatted sql

--changeset Vlad:20250708_005_1859

ALTER TABLE app.card_info
ADD COLUMN created_at TIMESTAMP(3);

ALTER TABLE app.card_info
ADD COLUMN updated_at TIMESTAMP(3);

-- filling in new columns with timestamps
UPDATE app.card_info
SET created_at = now(), updated_at = now()
WHERE created_at IS NULL OR updated_at IS NULL;

ALTER TABLE app.card_info
ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE app.card_info
ALTER COLUMN updated_at SET NOT NULL;