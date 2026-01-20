INSERT INTO tb_picture (name, path, type) VALUES ('1768850938328_1', 'C:\\workspace\\outros\\imagens\\1768850938328_1.png', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478289921_2', 'C:\\workspace\\outros\\imagens\\1768478289921_2.jpg', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478302771_3', 'C:\\workspace\\outros\\imagens\\1768478302771_3.jpg', 'PROFILE');

INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Daniel Vargas', 'ti@metaro.com.br', 'TI', '1999-07-08', 1, true, 'daniel.vargas','$2a$10$/38.DeKTEvwu4eCpOAe0GO298DNA7fXWvdPXDPMHe0bu/j53j5bA2', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Carlos Fronza', 'carlos.fronza@metaro.com.br', 'Compras', '1820-07-13', null, true, 'carlos.fronza','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Aline Moterle', 'rh@metaro.com.br', 'Recursos Humanos', '1987-11-25', 2, true, 'aline.moterle','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Luana Kohlrausch', 'luana.k@metaro.com.br', 'Recursos Humanos', '1998-07-14', null, true, 'luana.kohlrausch','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Juliano Bortoletti', 'marketing@metaro.com.br', 'Marketing', '1987-11-25', 3, true, 'juliano.bortoletti','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_user (name, email, position, birth_date, picture_id, activated, username, password, created_at, update_at)VALUES ('Teste Teste', 'teste@metaro.com.br', 'Teste', '1998-07-14', null, false, 'teste-teste','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');

INSERT INTO tb_role (authority) VALUES ('ROLE_USER');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (authority, title, parent) VALUES ('ROLE_ADM_PANEL','Usuários','Gestão');
INSERT INTO tb_role (authority, title, parent) VALUES ('ROLE_RAW_MATERIALS','Matérias primas','Geral');
INSERT INTO tb_role (authority, title, parent) VALUES ('ROLE_TODO','Para fazer','Geral');
INSERT INTO tb_role (authority, title, parent) VALUES ('ROLE_CHECKLIST','Checklist','Qualidade');
INSERT INTO tb_role (authority, title, parent) VALUES ('ROLE_INTERNAL_CONTROL', 'Controle Interno', 'Geral');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1,2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,4);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,5);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3,5);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4,5);
INSERT INTO tb_user_role (user_id, role_id) VALUES (5,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (5,5);
INSERT INTO tb_user_role (user_id, role_id) VALUES (6,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (6,5);

INSERT INTO tb_notification (title, is_new, user_id, created_at) VALUES ('CI 3921 - Erro...', true, 1, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_notification (title, is_new, user_id, created_at) VALUES ('Checklist pendente...', true, 1, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_notification (title, is_new, user_id, created_at) VALUES ('Novo evento!!', true, 1, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');

INSERT INTO tb_post (content, user_id, created_at, update_at)  VALUES ('Na próxima semana daremos continuidade as vivências do SESI, esta é uma excelente oportunidade para melhorar o bem-estar e a ergonomia no ambiente de trabalho.', 3, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');
INSERT INTO tb_post (content, user_id, created_at, update_at)  VALUES ('Hoje tivemos uma excelente reunião com o time de desenvolvimento. Grandes ideias surgiram para o nosso próximo sistema interno!', 5, TIMESTAMP WITH TIME ZONE '2025-05-01T10:00:00Z', TIMESTAMP WITH TIME ZONE '2025-05-01T10:00:00Z');
