INSERT INTO tb_param (id, name, content) VALUES (1, 'version', '0.14.1');
INSERT INTO tb_param (id, name, content) VALUES (2, 'internalControlCount', '357');
INSERT INTO tb_param (id, name, content) VALUES (3, 'currentYear', '2025');

INSERT INTO tb_picture (name, path, type) VALUES ('1768850938328_1', 'C:\\workspace\\outros\\imagens\\1768850938328_1.png', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478289921_2', 'C:\\workspace\\outros\\imagens\\1768478289921_2.jpg', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478302771_3', 'C:\\workspace\\outros\\imagens\\1768478302771_3.jpg', 'PROFILE');

INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('TI', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Compras', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Recursos Humanos', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Marketing', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Pós Vendas', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Engenharia', false, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Financeiro', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Comercial', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Qualidade', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Almoxarifado', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('PCP', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Produção', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Manutenção', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Corte/Dobra', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Montagem/Solda', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('TST', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Direção', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');

INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Daniel Vargas', 'ti@metaro.com.br', 1, '1999-07-08', 1, true, 'daniel-vargas','$2a$10$/38.DeKTEvwu4eCpOAe0GO298DNA7fXWvdPXDPMHe0bu/j53j5bA2', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Carlos Fronza', 'carlos.fronza@metaro.com.br', 2, '1820-07-13', null, true, 'carlos-fronza','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', 'uTz4Ch9QFCq120pnAmMAU0eXgEC14MHkY0TnNdIu');
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Aline Moterle', 'rh@metaro.com.br', 3, '1987-11-25', 2, true, 'aline-moterle','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Luana Kohlrausch', 'luana.k@metaro.com.br', 3, '1998-07-14', null, true, 'luana-kohlrausch','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Juliano Bortoletti', 'marketing@metaro.com.br', 4, '1987-11-25', 3, true, 'juliano-bortoletti','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Adriano Miolo', 'compras@metaro.com.br', 2, '1992-07-14', null, true, 'adriano-miolo','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);

INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 1);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 2);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (2, 2);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 3);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (3, 3);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 4);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (5, 4);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 5);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 7);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 8);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 9);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 10);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 11);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 12);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 13);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 16);
INSERT INTO tb_position_manangers (mananger_id, position_id) VALUES (1, 17);

INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_USER');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (3, 'ROLE_ADM_PANEL', 'Usuários', '/users', 'Administração','/administration');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (4, 'ROLE_RAW_MATERIALS', 'Matérias primas', '/raw-materials', 'Geral','/general');
--INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (5, 'ROLE_TODO', 'Para fazer', '/todo', 'Geral', '/general');
--INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (6, 'ROLE_CHECKLIST', 'Checklist', '/checklist', 'Qualidade', '/quality');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (7, 'ROLE_MEMORANDO', 'Memorando', '/memorando', 'Geral', '/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (8, 'ROLE_POSITION_PANEL', 'Departamentos', '/departments', 'Administração','/administration');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1,2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,7);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (5,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (6,1);

INSERT INTO tb_notification (title, is_new, user_id, created_by_user_id, created_at) VALUES ('Novo evento!!', true, 1, 3, TIMESTAMP WITH TIME ZONE '2026-01-21T11:00:00Z');
INSERT INTO tb_notification (title, is_new, user_id, created_by_user_id, created_at) VALUES ('2 Checklist pendentes...', true, 1, null, TIMESTAMP WITH TIME ZONE '2026-01-21T09:00:00Z');
INSERT INTO tb_notification (title, is_new, user_id, created_by_user_id, created_at) VALUES ('CI 3921 - Equipamento estruturado com a cor errada.', false, 1, 2, TIMESTAMP WITH TIME ZONE '2026-01-19T15:00:00Z');
