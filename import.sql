-- ==========================================
-- Crear usuario admin
-- ==========================================
INSERT INTO jhi_user (id, login, password, activated, email, lang_key, created_by, created_date, last_modified_by, last_modified_date)
VALUES (nextval('jhi_user_id_seq'), 'admin', '$2a$10$k1vHDk6C9zXpi9vQh7jGEOgip/qY6svQsS/8s6cOpoB7Kz7gEiVvG', true, 'admin@example.com', 'en', 'system', now(), 'system', now());

INSERT INTO jhi_user_authority (user_id, authority_name)
VALUES (currval('jhi_user_id_seq'), 'ROLE_ADMIN');

-- ==========================================
-- Crear usuario normal
-- ==========================================
INSERT INTO jhi_user (id, login, password, activated, email, lang_key, created_by, created_date, last_modified_by, last_modified_date)
VALUES (nextval('jhi_user_id_seq'), 'user', '$2a$10$k1vHDk6C9zXpi9vQh7jGEOgip/qY6svQsS/8s6cOpoB7Kz7gEiVvG', true, 'user@example.com', 'en', 'system', now(), 'system', now());

INSERT INTO jhi_user_authority (user_id, authority_name)
VALUES (currval('jhi_user_id_seq'), 'ROLE_USER');