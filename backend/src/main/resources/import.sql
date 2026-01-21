INSERT INTO tb_picture (name, path, type) VALUES ('1768850938328_1', 'C:\\workspace\\outros\\imagens\\1768850938328_1.png', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478289921_2', 'C:\\workspace\\outros\\imagens\\1768478289921_2.jpg', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478302771_3', 'C:\\workspace\\outros\\imagens\\1768478302771_3.jpg', 'PROFILE');

INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Daniel Vargas', 'ti@metaro.com.br', 'TI', '1999-07-08', 1, true, 'daniel-vargas','$2a$10$/38.DeKTEvwu4eCpOAe0GO298DNA7fXWvdPXDPMHe0bu/j53j5bA2', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Carlos Fronza', 'carlos.fronza@metaro.com.br', 'Compras', '1820-07-13', null, true, 'carlos-fronza','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Aline Moterle', 'rh@metaro.com.br', 'Recursos Humanos', '1987-11-25', 2, true, 'aline-moterle','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Luana Kohlrausch', 'luana.k@metaro.com.br', 'Recursos Humanos', '1998-07-14', null, true, 'luana-kohlrausch','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Juliano Bortoletti', 'marketing@metaro.com.br', 'Marketing', '1987-11-25', 3, true, 'juliano-bortoletti','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Adriano Miolo', 'compras@metaro.com.br', 'Compras', '1992-07-14', null, true, 'adriano-miolo','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');

INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_USER');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (3, 'ROLE_ADM_PANEL', 'Usuários', '/users', 'Gestão','/administration');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (4, 'ROLE_RAW_MATERIALS', 'Matérias primas', '/raw-materials', 'Geral','/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (5, 'ROLE_TODO', 'Para fazer', '/todo', 'Geral', '/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (6, 'ROLE_CHECKLIST', 'Checklist', '/checklist', 'Qualidade', '/quality');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (7, 'ROLE_INTERNAL_CONTROL', 'Controle Interno', '/internal-control', 'Geral', '/general');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1,2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,4);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,5);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,7);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (5,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (6,1);

INSERT INTO tb_notification (title, is_new, user_id, created_by_user_id, created_at) VALUES ('Novo evento!!', true, 1, 3, TIMESTAMP WITH TIME ZONE '2026-01-21T11:00:00Z');
INSERT INTO tb_notification (title, is_new, user_id, created_by_user_id, created_at) VALUES ('2 Checklist pendentes...', true, 1, null, TIMESTAMP WITH TIME ZONE '2026-01-21T09:00:00Z');
INSERT INTO tb_notification (title, is_new, user_id, created_by_user_id, created_at) VALUES ('CI 3921 - Equipamento estruturado com a cor errada.', false, 1, 2, TIMESTAMP WITH TIME ZONE '2026-01-19T15:00:00Z');

--INSERT INTO tb_post (content, user_id, created_at, update_at)  VALUES ('Na próxima semana daremos continuidade as vivências do SESI, esta é uma excelente oportunidade para melhorar o bem-estar e a ergonomia no ambiente de trabalho.', 3, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
--INSERT INTO tb_post (content, user_id, created_at, update_at)  VALUES ('Hoje tivemos uma excelente reunião com o time de desenvolvimento. Grandes ideias surgiram para o nosso próximo sistema interno!', 5, TIMESTAMP WITH TIME ZONE '2025-05-01T10:00:00Z', TIMESTAMP WITH TIME ZONE '2025-05-01T10:00:00Z');
