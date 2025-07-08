--liquibase formatted sql

--changeset Vlad:20250629_002_1330

CREATE TABLE app.card_info
(
id UUID,
user_id BIGINT,
number VARCHAR(16) NOT NULL,
holder VARCHAR(64) NOT NULL,
expiration_date DATE NOT NULL,
CONSTRAINT card_info_id_pk PRIMARY KEY(id),
CONSTRAINT card_info_users_fk FOREIGN KEY(user_id) REFERENCES app.users(id),
CONSTRAINT card_info_number_unq UNIQUE(number)
);

ALTER TABLE IF EXISTS app.card_info
    OWNER to postgres;