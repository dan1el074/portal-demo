INSERT INTO tb_param (id, name, content) VALUES (1, 'version', '0.14.1');
INSERT INTO tb_param (id, name, content) VALUES (2, 'internalControlCount', '357');
INSERT INTO tb_param (id, name, content) VALUES (3, 'currentYear', '2025');

INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('TI', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');

INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Master', 'ti@metaro.com.br', 1, '1999-07-08', NULL, true, 'master','$2a$10$/38.DeKTEvwu4eCpOAe0GO298DNA7fXWvdPXDPMHe0bu/j53j5bA2', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);

INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_USER');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (3, 'ROLE_ADM_PANEL', 'Usuários', '/users', 'Administração','/administration');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (4, 'ROLE_POSITION_PANEL', 'Departamentos', '/departments', 'Administração','/administration');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (5, 'ROLE_TODO', 'Para fazer', '/todo', 'Geral', '/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (6, 'ROLE_MEMORANDO', 'Memorando', '/memorando', 'Geral', '/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (7, 'ROLE_RAW_MATERIALS', 'Matérias primas', '/raw-materials', 'Geral','/general');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1,2);
