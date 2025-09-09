INSERT INTO app.users(id, name, surname, birth_date, email, version, created_at, updated_at, auth_id)
VALUES
(1, 'Frodo', 'Baggins', '1997-07-07', 'frodo_baggins@email.com', 0, '2025-08-07 19:30:00', '2025-08-07 19:30:00',1),
(2, 'Samwise', 'Gampgie', '1999-11-01', 'sam_gampgie@email.com', 0, '2025-08-07 19:30:00', '2025-08-07 19:30:00',2),
(3, 'Arya', 'Stark', '1993-01-01', 'arya_stark@email.com', 0, '2025-08-07 19:30:00', '2025-08-07 19:30:00',3);

SELECT SETVAL('users_id_seq', (SELECT MAX(id) FROM app.users));

INSERT INTO app.card_info(id, user_id, number, holder, expiration_date, version, created_at, updated_at)
VALUES
('cbf7e645-523c-458e-a4bc-c8c01bb3d82a', (SELECT id FROM app.users WHERE name = 'Frodo' AND surname = 'Baggins'),
'2222444499991111','FRODO BAGGINS', '2025-12-31', 0, '2025-08-07 19:30:00', '2025-08-07 19:30:00'),

('dd7844c4-4746-4ce0-a665-fb7388755865', (SELECT id FROM app.users WHERE name = 'Frodo' AND surname = 'Baggins'),
'2222111144447777','FRODO BAGGINS', '2029-06-30', 0, '2025-08-07 19:30:00', '2025-08-07 19:30:00'),

('8f1958d9-8c55-4f86-be31-9060b5feb7c6', (SELECT id FROM app.users WHERE name = 'Frodo' AND surname = 'Baggins'),
'2222333344446666','FRODO BAGGINS', '2027-03-31', 0, '2025-08-07 19:30:00', '2025-08-07 19:30:00'),

('83aa085d-e6f6-4246-bc98-c536a006641c', (SELECT id FROM app.users WHERE name = 'Samwise' AND surname = 'Gampgie'),
'333377775559999','SAMWISE GAMPGIE', '2028-08-31', 0, '2025-08-07 19:30:00', '2025-08-07 19:30:00');
