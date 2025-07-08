--liquibase formatted sql

--changeset Vlad:20250630_003_1150

CREATE INDEX card_info_user_id_idx ON app.card_info(user_id);