INSERT INTO tb_param (id, name, content) VALUES (1, 'version', '0.14.1');
INSERT INTO tb_param (id, name, content) VALUES (2, 'internalControlCount', '357');
INSERT INTO tb_param (id, name, content) VALUES (3, 'currentYear', '2025');

INSERT INTO tb_picture (name, path, type) VALUES ('1768850938328_1', 'C:\\workspace\\outros\\imagens\\1768850938328_1.png', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478289921_2', 'C:\\workspace\\outros\\imagens\\1768478289921_2.jpg', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478302771_3', 'C:\\workspace\\outros\\imagens\\1768478302771_3.jpg', 'PROFILE');

INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (1, 'TI', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (2, 'Compras', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (3, 'Recursos Humanos', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (4, 'Marketing', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (5, 'Pós Vendas', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (6, 'Engenharia', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (7, 'Financeiro', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (8, 'Comercial', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (9, 'Qualidade', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (10, 'Almoxarifado', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (11, 'PCP', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (12, 'Produção', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (13, 'Manutenção', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (14, 'Corte/Dobra', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (15, 'Montagem/Solda', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (16, 'TST', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, mananger_id, updated_at, created_at) VALUES (17, 'Direção', null, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');

INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Daniel Vargas', 'ti@metaro.com.br', 1, '1999-07-08', 1, true, 'daniel-vargas','$2a$10$/38.DeKTEvwu4eCpOAe0GO298DNA7fXWvdPXDPMHe0bu/j53j5bA2', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Carlos Fronza', 'carlos.fronza@metaro.com.br', 2, '1820-07-13', null, true, 'carlos-fronza','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', 'uTz4Ch9QFCq120pnAmMAU0eXgEC14MHkY0TnNdIu');
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Aline Moterle', 'rh@metaro.com.br', 3, '1987-11-25', 2, true, 'aline-moterle','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Luana Kohlrausch', 'luana.k@metaro.com.br', 3, '1998-07-14', null, true, 'luana-kohlrausch','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Juliano Bortoletti', 'marketing@metaro.com.br', 4, '1987-11-25', 3, true, 'juliano-bortoletti','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Adriano Miolo', 'compras@metaro.com.br', 2, '1992-07-14', null, true, 'adriano-miolo','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);

INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_USER');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (3, 'ROLE_ADM_PANEL', 'Usuários', '/users', 'Gestão','/administration');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (4, 'ROLE_RAW_MATERIALS', 'Matérias primas', '/raw-materials', 'Geral','/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (5, 'ROLE_TODO', 'Para fazer', '/todo', 'Geral', '/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (6, 'ROLE_CHECKLIST', 'Checklist', '/checklist', 'Qualidade', '/quality');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (7, 'ROLE_INTERNAL_COMMUNICATION', 'Comunicação Interna', '/internal-communication', 'Geral', '/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (8, 'ROLE_POSITION_PANEL', 'Departamentos', '/departments', 'Gestão','/administration');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1,2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,4);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,7);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (5,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (6,1);

INSERT INTO tb_notification (title, is_new, user_id, created_by_user_id, created_at) VALUES ('Novo evento!!', true, 1, 3, TIMESTAMP WITH TIME ZONE '2026-01-21T11:00:00Z');
INSERT INTO tb_notification (title, is_new, user_id, created_by_user_id, created_at) VALUES ('2 Checklist pendentes...', true, 1, null, TIMESTAMP WITH TIME ZONE '2026-01-21T09:00:00Z');
INSERT INTO tb_notification (title, is_new, user_id, created_by_user_id, created_at) VALUES ('CI 3921 - Equipamento estruturado com a cor errada.', false, 1, 2, TIMESTAMP WITH TIME ZONE '2026-01-19T15:00:00Z');

INSERT INTO tb_internal_communication (number, request, client, item, title, description, reason, create_at, status, user_id) VALUES (357, 12080, 'KOVALENT DO BRASIL LTDA', '46831 - ME MT-302030 C17 EH GC BAE SANF PE500 F600 P12080', 'RETRABALHAR PLATAFORMA', 'Retrabalhar suporte da mola da pestana.<br><br>Necessário recortar e retrabalhar o suporte da mola a gas de acionamento da pestana.<br><br>Fabricar:<br>40128 - PRF U #6,35 90x80 L70mm - 01 PEÇA<br>11098 - TB A53 #2,00 50x100 L245mm - 01 PEÇA', 'Posicionamento incorreto no projeto', TIMESTAMP WITH TIME ZONE '2025-01-19T15:00:00Z', 'APPROVED', 2);

INSERT INTO tb_internal_communication_departments (internal_communication_id, departments_id) VALUES (1, 2);
INSERT INTO tb_internal_communication_departments (internal_communication_id, departments_id) VALUES (1, 1);

INSERT INTO tb_interaction (internal_communication_id, user_id, position_signed_id, created_at) VALUES (1, 2, 2, TIMESTAMP WITH TIME ZONE '2026-02-09T09:30:00Z');
INSERT INTO tb_interaction (internal_communication_id, user_id, position_signed_id, created_at) VALUES (1, 1, 1, TIMESTAMP WITH TIME ZONE '2026-02-09T09:30:00Z');

INSERT INTO tb_internal_communication_log (internal_communication_id, content, user_id, created_at) VALUES (1, 'Criou o documento', 2, TIMESTAMP WITH TIME ZONE '2026-02-04T16:30:32Z');
INSERT INTO tb_internal_communication_log (internal_communication_id, content, user_id, created_at) VALUES (1, 'Publicou o documento nº 372/2026', 2, TIMESTAMP WITH TIME ZONE '2026-02-04T16:32:11Z');
INSERT INTO tb_internal_communication_log (internal_communication_id, content, user_id, created_at) VALUES (1, 'Assinou o documento', 2, TIMESTAMP WITH TIME ZONE '2026-02-04T17:11:27Z');
INSERT INTO tb_internal_communication_log (internal_communication_id, content, user_id, created_at) VALUES (1, 'Alterou o título de "Acabamento inadequado" para "Pintura inadequado"', 1, TIMESTAMP WITH TIME ZONE '2026-02-04T16:46:33Z');
INSERT INTO tb_internal_communication_log (internal_communication_id, content, user_id, created_at) VALUES (1, 'Assinou o documento', 1, TIMESTAMP WITH TIME ZONE '2026-02-04T17:10:56Z');
INSERT INTO tb_internal_communication_log (internal_communication_id, content, user_id, created_at) VALUES (1, 'Assinou o documento', 2, TIMESTAMP WITH TIME ZONE '2026-02-04T17:11:27Z');
INSERT INTO tb_internal_communication_log (internal_communication_id, content, user_id, created_at) VALUES (1, 'Documento nº 372/2026 aprovado por todas as áreas', null, TIMESTAMP WITH TIME ZONE '2026-02-04T17:11:41Z');
