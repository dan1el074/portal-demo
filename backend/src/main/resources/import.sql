INSERT INTO tb_param (id, name, content) VALUES (1, 'version', '0.7.1');
INSERT INTO tb_param (id, name, content) VALUES (2, 'memorandoCount', '174');
INSERT INTO tb_param (id, name, content) VALUES (3, 'memorandoYear', '2026');

INSERT INTO tb_post (id, content, created_at, updated_at) VALUES (1, 'Hoje à tarde o RH juntamente com os gestores da área (Chailan e Diogo), conduziram a primeira etapa do processo seletivo de Recrutamento Interno para a vaga de líder de produção.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO tb_post (id, content, created_at, updated_at) VALUES (2, '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO tb_post (id, content, created_at, updated_at) VALUES (3, 'Hoje é o <strong>Dia da Família</strong>, e queremos lembrar que ela vai muito além dos laços de sangue. Família é onde encontramos apoio, afeto, cuidado e verdade.<br><br>Feliz Dia da Família!<br>Equipe Metaro.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO tb_post (id, content, created_at, updated_at) VALUES (4, '<strong>Sobre nossa energia emocional:</strong><br><strong>- Gratidão:</strong> Aprecie as qualidades dos outros e veja o lado bom em cada situação. Seja grato por tudo e todos, sempre fazendo o que é certo!<br><strong>- Ambiente estimulante: </strong> Crie um ambiente leve, positivo e criativo. Para nós, é essencial trabalharmos em um lugar agradável!<br><strong>- Franqueza: </strong> Aja com franqueza e mostre seus sentimentos de maneira aberta e amigável!<br>Nós acreditamos que estamos criando a melhor empresa para se trabalhar! 🚀', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tb_picture (name, path, type) VALUES ('1768850938328_1', 'C:\\workspace\\outros\\imagens\\1768850938328_1.png', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1774469447392_2', 'C:\\workspace\\outros\\imagens\\1774469447392_2.png', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478302771_3', 'C:\\workspace\\outros\\imagens\\1768478302771_3.jpg', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1774469447392_4', 'C:\\workspace\\outros\\imagens\\1774469447392_4.png', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478289921_5', 'C:\\workspace\\outros\\imagens\\1768478289921_5.jpg', 'PROFILE');
INSERT INTO tb_picture (name, path, type) VALUES ('1768478289921_6', 'C:\\workspace\\outros\\imagens\\1774469447367_6.webp', 'EVENT');
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00001', 'C:\\workspace\\outros\\imagens\\img_00001.jpg', 'POST', 1);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00002', 'C:\\workspace\\outros\\imagens\\img_00002.jpg', 'POST', 1);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00003', 'C:\\workspace\\outros\\imagens\\img_00003.jpg', 'POST',1 );
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00004', 'C:\\workspace\\outros\\imagens\\img_00004.jpg', 'POST',1);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00005', 'C:\\workspace\\outros\\imagens\\img_00005.jpg', 'POST',1);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00006', 'C:\\workspace\\outros\\imagens\\img_00006.jpeg', 'POST', 3);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00007', 'C:\\workspace\\outros\\imagens\\img_00007.jpeg', 'POST', 2);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00008', 'C:\\workspace\\outros\\imagens\\img_00008.jpeg', 'POST', 4);

INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('TI', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Compras', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Recursos Humanos', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Marketing', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Engenharia', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Qualidade', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Almoxarifado', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Montagem/Solda', false, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Produção', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Manutenção', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (name, activated, updated_at, created_at) VALUES ('Corte/Dobra', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');

INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Daniel Vargas', 'daniel.vargas@metaro.com.br', 1, '1999-06-08', 1, true, 'daniel-vargas','$2a$10$/38.DeKTEvwu4eCpOAe0GO298DNA7fXWvdPXDPMHe0bu/j53j5bA2', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Enzo Bazzi', 'enzo.bazzi@metaro.com.br', 1, '2003-06-02', 2, true, 'adriano-miolo','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Aline Moterle', 'rh@metaro.com.br', 3, '1987-11-25', 3, true, 'aline-moterle','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Carlos Fronza', 'carlos.fronza@metaro.com.br', 2, '1820-06-13', 4, true, 'carlos-fronza','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', 'uTz4Ch9QFCq120pnAmMAU0eXgEC14MHkY0TnNdIu');
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Juliano Bortoletti', 'marketing@metaro.com.br', 4, '1987-11-25', 5, true, 'juliano-bortoletti','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Luana Kohlrausch', 'luana.k@metaro.com.br', 3, '1998-07-14', null, true, 'luana-kohlrausch','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);

UPDATE tb_post SET user_id = 3 WHERE id = 1;
UPDATE tb_post SET user_id = 3 WHERE id = 2;
UPDATE tb_post SET user_id = 5 WHERE id = 3;
UPDATE tb_post SET user_id = 3 WHERE id = 4;

INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (1, 1);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (2, 1);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (2, 4);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (3, 1);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (4, 1);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (4, 5);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (5, 1);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (6, 1);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (7, 1);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (8, 1);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (9, 1);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (10, 1);
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (11, 1);

INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_USER');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (3, 'ROLE_ADM_PANEL', 'Usuários', '/users', 'Administração','/administration');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (4, 'ROLE_POSITION_PANEL', 'Departamentos', '/departments', 'Administração','/administration');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (5, 'ROLE_RAW_MATERIALS', 'Matérias primas', '/raw-materials', 'Geral','/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (6, 'ROLE_MEMORANDO', 'Memorando', '/memorando', 'Geral', '/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (7, 'ROLE_MES', 'MES Metaro', '/mes', 'Apps', '/apps');
INSERT INTO tb_role (id, authority) VALUES (8, 'ROLE_MES_ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1,2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,5);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2,6);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4,6);
INSERT INTO tb_user_role (user_id, role_id) VALUES (5,1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (6,1);

INSERT INTO tb_notification (message, action_url, viewed, auto_delete, type, reference_id, created_at, user_id) VALUES ('teste', null, false, true, 'MEMORANDO_FINISH', null, NOW(), 1);
INSERT INTO tb_notification (message, action_url, viewed, auto_delete, type, reference_id, created_at, user_id) VALUES ('teste', null, false, true, 'MEMORANDO_FINISH', null, NOW(), 1);
INSERT INTO tb_notification (message, action_url, viewed, auto_delete, type, reference_id, created_at, user_id) VALUES ('teste', null, false, true, 'MEMORANDO_FINISH', null, NOW(), 1);
INSERT INTO tb_notification (message, action_url, viewed, auto_delete, type, reference_id, created_at, user_id) VALUES ('teste', null, false, true, 'MEMORANDO_FINISH', null, NOW(), 1);
INSERT INTO tb_notification (message, action_url, viewed, auto_delete, type, reference_id, created_at, user_id) VALUES ('teste', null, false, true, 'MEMORANDO_FINISH', null, NOW(), 1);
INSERT INTO tb_notification (message, action_url, viewed, auto_delete, type, reference_id, created_at, user_id) VALUES ('teste', null, false, true, 'MEMORANDO_FINISH', null, NOW(), 1);
INSERT INTO tb_notification (message, action_url, viewed, auto_delete, type, reference_id, created_at, user_id) VALUES ('teste', null, false, true, 'MEMORANDO_FINISH', null, NOW(), 1);
INSERT INTO tb_notification (message, action_url, viewed, auto_delete, type, reference_id, created_at, user_id) VALUES ('teste', null, false, true, 'MEMORANDO_FINISH', null, NOW(), 1);
INSERT INTO tb_notification (message, action_url, viewed, auto_delete, type, reference_id, created_at, user_id) VALUES ('teste', null, false, true, 'MEMORANDO_FINISH', null, NOW(), 1);

INSERT INTO tb_file (id, title, file_name, created_at) VALUES (1, 'Manifesto', 'manifesto.png', CURRENT_TIMESTAMP);
INSERT INTO tb_file (id, title, file_name, created_at) VALUES (2, 'Lista de ramais', 'ramais.pdf', CURRENT_TIMESTAMP);
INSERT INTO tb_file (id, title, file_name, created_at) VALUES (3, 'Memorando', 'manual-memorando.pdf', CURRENT_TIMESTAMP);

INSERT INTO tb_event (title, picture_id, event_date, created_at, updated_at) VALUES ('Minuto da Qualidade', 6, TIMESTAMP WITH TIME ZONE '2026-06-09T15:00:00Z', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
