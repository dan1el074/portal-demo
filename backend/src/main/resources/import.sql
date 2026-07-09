INSERT INTO tb_param (id, name, content) VALUES (1, 'version', '0.7.1');
INSERT INTO tb_param (id, name, content) VALUES (2, 'memorandoCount', '174');
INSERT INTO tb_param (id, name, content) VALUES (3, 'memorandoYear', '2026');

INSERT INTO tb_post (content, is_warning, created_at, updated_at) VALUES ('<strong>Sobre nossa energia emocional:</strong><br><strong>- Gratidão:</strong> Aprecie as qualidades dos outros e veja o lado bom em cada situação. Seja grato por tudo e todos, sempre fazendo o que é certo!<br><strong>- Ambiente estimulante: </strong> Crie um ambiente leve, positivo e criativo. Para nós, é essencial trabalharmos em um lugar agradável!<br><strong>- Franqueza: </strong> Aja com franqueza e mostre seus sentimentos de maneira aberta e amigável!<br>Nós acreditamos que estamos criando a melhor empresa para se trabalhar! 🚀', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO tb_post (content, is_warning, created_at, updated_at) VALUES ('Hoje é o <strong>Dia da Família</strong>, e queremos lembrar que ela vai muito além dos laços de sangue. Família é onde encontramos apoio, afeto, cuidado e verdade.<br><br>Feliz Dia da Família!<br>Equipe Metaro.', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO tb_post (content, is_warning, created_at, updated_at) VALUES ('', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO tb_post (content, is_warning, created_at, updated_at) VALUES ('Hoje à tarde o RH juntamente com os gestores da área (Chailan e Diogo), conduziram a primeira etapa do processo seletivo de Recrutamento Interno para a vaga de líder de produção.', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tb_picture (name, path, type, post_id) VALUES ('1768850938328_1', 'C:\\workspace\\outros\\imagens\\1768850938328_1.png', 'PROFILE', NULL);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('1774469447392_2', 'C:\\workspace\\outros\\imagens\\1774469447392_2.png', 'PROFILE', NULL);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('1768478302771_3', 'C:\\workspace\\outros\\imagens\\1768478302771_3.jpg', 'PROFILE', NULL);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('1774469447392_4', 'C:\\workspace\\outros\\imagens\\1774469447392_4.png', 'PROFILE', NULL);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('1768478289921_5', 'C:\\workspace\\outros\\imagens\\1768478289921_5.jpg', 'PROFILE', NULL);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('1768478289921_6', 'C:\\workspace\\outros\\imagens\\1774469447367_6.webp', 'EVENT', NULL);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00001', 'C:\\workspace\\outros\\imagens\\img_00001.jpg', 'POST', 4);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00002', 'C:\\workspace\\outros\\imagens\\img_00002.jpg', 'POST', 4);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00003', 'C:\\workspace\\outros\\imagens\\img_00003.jpg', 'POST', 4);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00004', 'C:\\workspace\\outros\\imagens\\img_00004.jpg', 'POST',4);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00005', 'C:\\workspace\\outros\\imagens\\img_00005.jpg', 'POST',4);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00006', 'C:\\workspace\\outros\\imagens\\img_00006.jpeg', 'POST', 2);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00007', 'C:\\workspace\\outros\\imagens\\img_00007.jpeg', 'POST', 3);
INSERT INTO tb_picture (name, path, type, post_id) VALUES ('img_00008', 'C:\\workspace\\outros\\imagens\\img_00008.jpeg', 'POST', 1);

INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (1, 'TI', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (2, 'Compras', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (3, 'Recursos Humanos', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (4, 'Marketing', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (5, 'Engenharia', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (6, 'Qualidade', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (7, 'PCP', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (8, 'Comercial', false, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (9, 'Produção', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (10, 'Almoxarifado', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (11, 'Montagem Final', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');
INSERT INTO tb_position (id, name, activated, updated_at, created_at) VALUES (19, 'Financeiro', true, TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z', TIMESTAMP WITH TIME ZONE '2026-02-23T15:00:00Z');

INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Daniel Vargas', 'daniel.vargas@metaro.com.br', 1, '1999-06-08', 1, true, 'daniel-vargas','$2a$10$/38.DeKTEvwu4eCpOAe0GO298DNA7fXWvdPXDPMHe0bu/j53j5bA2', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
INSERT INTO tb_user (name, email, position_id, birth_date, picture_id, activated, username, password, created_at, update_at, support_token) VALUES ('Enzo Bazzi', 'enzo.bazzi@metaro.com.br', 1, '2003-06-09', 2, true, 'adriano-miolo','$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', null);
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
INSERT INTO tb_position_manangers (position_id, mananger_id) VALUES (19, 1);

INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_USER');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (3, 'ROLE_ADM_PANEL', 'Usuários', '/users', 'Administração','/administration');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (4, 'ROLE_POSITION_PANEL', 'Departamentos', '/departments', 'Administração','/administration');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (5, 'ROLE_RAW_MATERIALS', 'Matérias primas', '/raw-materials', 'Geral','/general');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (6, 'ROLE_MEMORANDO', 'Memorando', '/memorando', 'Geral', '/general');
INSERT INTO tb_role (id, authority, title, parent) VALUES (7, 'ROLE_POST', 'Postagens', 'Administração');
INSERT INTO tb_role (id, authority, title, title_url, parent, parent_url) VALUES (8, 'ROLE_STEP_FLOW', 'Fluxo de etapas', '/step-flow', 'Geral', '/general');
INSERT INTO tb_role (id, authority, title, parent, father_id) VALUES (9, 'ROLE_STEP_FLOW_ADMIN', 'Fluxo de etapas - Admin', 'Geral', 8);

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

INSERT INTO tb_event (title, picture_id, event_date, created_at, updated_at) VALUES ('Minuto da Qualidade', 6, TIMESTAMP WITH TIME ZONE '2026-07-14T20:15:00Z', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

---- ORDER 1
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10424, 'Entrega urgente solicitada pelo cliente', 'PCP', 'IN_PROGRESS', 'Indústria Bertucci S.A.', '12.345.678/0001-90', '(54) 99821-4477', 'Mateus Bottega', '2026-06-30', '2026-06-30', 'Av. Industrial Anhembi, 2,841 - Galpão 7, Osasco/SP, CEP 706.273-000', 1000.0, 300.0, 125.5, 874.5, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 1, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'ACTIVE', NOW(), NOW(), 1, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'WAITING', NOW(), NOW(), 1, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'WAITING', NOW(), NOW(), 1, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'WAITING', NOW(), NOW(), 1, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('4821', 'Redutor de Velocidade Industrial 7,5kW', 'un', '2', 8450.0, 16900.0, 1);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('1193', 'Motor Trifásico WEG 220/380V 15CV', 'un', '1', 12300.0, 12300.0, 1);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('0044', 'Correia em V SPC 3350 (jogo c/ 5)', 'jg', '3', 890.0, 2670.0, 1);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('2281', 'Serviço de Instalação e Comissionamento', 'srv', '1', 4200.0, 4200.0, 1);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Ordem de produção gerada e lista de materiais conferida.', NOW(), 1, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Separação de peças iniciada automaticamente.', NOW(), 1, NULL);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('PCP validou BOM e liberou para frete.', NOW(), 2, 4);
--INSERT INTO tb_picture (name, path, type, post_id, step_id, size, created_at) VALUES ('img_00008', 'C:\\workspace\\outros\\imagens\\47744694473927_STEP_FLOW.jpg', 'POST', NULL, 1, '20 kb', NOW());
--
---- ORDER 2
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10425, 'Entrega urgente solicitada pelo cliente', 'FREIGHT', 'LATE', 'Confab Industrial', '98.765.432/0001-11', '(11) 98234-5678', 'Rafael Souza', '2026-07-01', '2026-07-01', 'Rua das Indústrias, 500 - Bloco B, São Paulo/SP, CEP 01310-100', 12880.0, 200.0, 300.0, 12980.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 2, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 2, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'ACTIVE', NOW(), NOW(), 2, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'WAITING', NOW(), NOW(), 2, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'WAITING', NOW(), NOW(), 2, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('3310', 'Bomba Centrífuga 2" x 1.1/2" 1CV', 'un', 4, 1850.0, 7400.0, 2);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('0872', 'Selo Mecânico para Bomba 1CV', 'un', 4, 320.0, 1280.0, 2);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('5541', 'Quadro de Comando DOL 220V 5CV', 'un', 2, 2100.0, 4200.0, 2);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Montagem concluída e testada em bancada sem defeitos.', NOW(), 6, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('PCP validou BOM e liberou para frete.', NOW(), 7, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Aguardando retorno da transportadora sobre prazo de coleta.', NOW(), 8, NULL);
--
---- ORDER 3
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10426, NULL, 'SHIPPING', 'COMPLETED', 'Grupo Votorantim Metais', '45.678.901/0001-23', '(19) 3211-4400', 'Carla Mendes', '2026-08-01', '2026-08-01', 'Rodovia Anhanguera, km 110, Limeira/SP, CEP 13480-000', 25770.0, 500.0, 420.0, 25690.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 3, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 3, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 3, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 3, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'DONE', NOW(), NOW(), 3, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('7720', 'Transportador de Correia 10m x 500mm', 'un', 1, 18500.0, 18500.0, 3);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('4412', 'Moto Redutor 0,75kW 1/20 220/380V', 'un', 2, 3200.0, 6400.0, 3);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('1108', 'Sensor Indutivo M18 NPN NA 8mm', 'un', 6, 145.0, 870.0, 3);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Todos os componentes montados e alinhados conforme projeto.', NOW(), 11, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Planejamento de produção encerrado sem divergências.', NOW(), 12, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Coleta realizada pela transportadora no prazo.', NOW(), 13, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Nota fiscal emitida e DANFE enviada ao cliente.', NOW(), 14, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Entrega confirmada e recibo assinado pelo cliente.', NOW(), 15, 4);
--
---- ORDER 4
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10427, 'Cliente solicitou NF antecipada', 'SHIPPING', 'IN_PROGRESS', 'WEG Equipamentos Elétricos', '84.429.695/0001-11', '(47) 3372-4000', 'Lucas Faria', '2026-07-01', '2026-07-01', 'Av. Prefeito Waldemar Grubba, 3300, Jaraguá do Sul/SC, CEP 89256-900', 41880.0, 1000.0, 800.0, 41680.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 4, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 4, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 4, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 4, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'ACTIVE', NOW(), NOW(), 4, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('9930', 'Painel Elétrico de Comando CLP 24E/16S', 'un', 1, 28500.0, 28500.0, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('3341', 'Cabo PP 4x6mm² (rolo 100m)', 'rl', 5, 980.0, 4900.0, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('0019', 'Disjuntor Motor 9-13A', 'un', 8, 210.0, 1680.0, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('2200', 'Serviço de Programação e Start-up CLP', 'srv', 1, 6800.0, 6800.0, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Montagem do painel finalizada e testada com sucesso.', NOW(), 16, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('BOM revisado e aprovado pelo engenheiro responsável.', NOW(), 17, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Frete contratado com transportadora homologada.', NOW(), 18, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('NF emitida antecipadamente conforme solicitação.', NOW(), 19, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Volume etiquetado e aguardando coleta no dock.', NOW(), 20, NULL);
--
---- ORDER 5
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10428, NULL, 'SHIPPING', 'COMPLETED', 'Embraer Componentes S.A.', '07.689.002/0001-89', '(12) 3927-1000', 'Fernanda Lima', '2026-06-21', '2026-06-21', 'Av. Brigadeiro Faria Lima, 2170, São José dos Campos/SP, CEP 12227-901', 26920.0, 2000.0, 1500.0, 26420.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 5, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 5, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 5, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 5, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'DONE', NOW(), NOW(), 5, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('6610', 'Estrutura Metálica Galvanizada 6m', 'un', 10, 1240.0, 12400.0, 5);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('4490', 'Chapa de Aço ABNT 1020 3/8" 3000x1200', 'un', 8, 890.0, 7120.0, 5);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('3302', 'Serviço de Soldagem MIG/MAG', 'hr', 40, 185.0, 7400.0, 5);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Soldagem concluída e aprovada pelo CQ.', NOW(), 21, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Planejamento encerrado, materiais alocados corretamente.', NOW(), 22, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Coleta realizada dentro do prazo contratado.', NOW(), 23, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('DANFE emitida e enviada por e-mail ao cliente.', NOW(), 24, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Entrega confirmada pelo almoxarife da Embraer.', NOW(), 25, 4);
--
---- ORDER 6
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10429, 'Aguardando liberação fiscal', 'SHIPPING', 'IN_PROGRESS', 'Ferbasa Ferroligas da Bahia', '15.071.387/0001-59', '(71) 3319-5000', 'Mateus Bottega', '2026-06-07', '2026-06-07', 'Rodovia BA-093, km 32, Pojuca/BA, CEP 48120-000', 7650.0, 400.0, 250.0, 7500.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 6, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 6, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 6, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 6, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'ACTIVE', NOW(), NOW(), 6, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('8810', 'Rolamento Autocompensador de Rolos 22318', 'un', 12, 420.0, 5040.0, 6);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('7723', 'Graxa Lithium EP2 Balde 20kg', 'bd', 3, 310.0, 930.0, 6);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('1145', 'Acoplamento Flexível Jaw 65mm', 'un', 6, 280.0, 1680.0, 6);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Itens montados e lubrificados conforme manual técnico.', NOW(), 26, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('PCP confirmou disponibilidade de estoque para todos os itens.', NOW(), 27, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Transportadora contratada, aguardando agendamento de coleta.', NOW(), 28, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Pendência fiscal com SEFAZ-BA em análise pelo contador.', NOW(), 29, NULL);
--
---- ORDER 7
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10430, 'Produção atrasada por falta de insumos', 'FINAL_ASSEMBLY', 'LATE', 'Companhia Siderúrgica Nacional', '33.042.730/0001-04', '(24) 3344-1000', 'Rafael Souza', '2026-08-08', '2026-08-08', 'Rod. Presidente Dutra, km 237, Volta Redonda/RJ, CEP 27255-901', 51680.0, 0.0, 600.0, 52280.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'ACTIVE', NOW(), NOW(), 7, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'WAITING', NOW(), NOW(), 7, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'WAITING', NOW(), NOW(), 7, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'WAITING', NOW(), NOW(), 7, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'WAITING', NOW(), NOW(), 7, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('5500', 'Forno de Tratamento Térmico Câmara 500L', 'un', 1, 45000.0, 45000.0, 7);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('3318', 'Termopar tipo K com Conector M12', 'un', 8, 185.0, 1480.0, 7);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('2290', 'Serviço de Instalação Elétrica', 'srv', 1, 5200.0, 5200.0, 7);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Montagem iniciada mas interrompida por falta de resistências.', NOW(), 31, NULL);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Compra emergencial de insumos aprovada pelo gestor.', NOW(), 31, NULL);
--
---- ORDER 8
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10431, NULL, 'SHIPPING', 'COMPLETED', 'Petrobras Distribuidora S.A.', '34.274.233/0001-02', '(21) 3224-4477', 'Carla Mendes', '2026-08-01', '2026-08-01', 'Av. República do Chile, 65, Rio de Janeiro/RJ, CEP 20031-912', 51875.0, 5000.0, 2000.0, 48875.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 8, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 8, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 8, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 8, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'DONE', NOW(), NOW(), 8, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('9010', 'Válvula de Esfera Inox 2" PN40', 'un', 20, 890.0, 17800.0, 8);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('7701', 'Manômetro Glicerina 0-10 bar 1/2" BSP', 'un', 15, 145.0, 2175.0, 8);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('4430', 'Tubo Inox 316L 2" SCH10 (barra 6m)', 'br', 30, 780.0, 23400.0, 8);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('1122', 'Serviço de Montagem e Testes Hidrostáticos', 'srv', 1, 8500.0, 8500.0, 8);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Montagem e testes hidrostáticos concluídos sem vazamentos.', NOW(), 36, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Planejamento encerrado com todos os materiais alocados.', NOW(), 37, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Frete realizado com veículo baú rastreado.', NOW(), 38, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('NF emitida e aprovada sem rejeição na SEFAZ.', NOW(), 39, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Entrega realizada e assinada pelo responsável da Petrobras.', NOW(), 40, 4);
--
---- ORDER 9
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10432, 'Frete cotado com 3 transportadoras', 'FREIGHT', 'IN_PROGRESS', 'Randon Implementos e Part.', '89.086.144/0001-16', '(54) 3209-2000', 'Lucas Faria', '2026-07-01', '2026-07-01', 'Rua Randon, 1200, Caxias do Sul/RS, CEP 95112-010', 21280.0, 300.0, 450.0, 21430.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 9, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 9, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'ACTIVE', NOW(), NOW(), 9, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'WAITING', NOW(), NOW(), 9, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'WAITING', NOW(), NOW(), 9, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('6620', 'Eixo Cardan 1350 L=1200mm', 'un', 4, 3800.0, 15200.0, 9);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('3390', 'Cruzeta 1350 com Rolamento', 'un', 8, 420.0, 3360.0, 9);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('0091', 'Flange Deslizante 1350 Fêmea', 'un', 4, 680.0, 2720.0, 9);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Cardans montados e balanceados dinamicamente.', NOW(), 41, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('BOM conferido e liberado pelo PCP.', NOW(), 42, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Melhor cotação selecionada, aguardando emissão de CTE.', NOW(), 43, NULL);
--
---- ORDER 10
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10433, NULL, 'SHIPPING', 'COMPLETED', 'Marcopolo S.A.', '88.611.835/0001-29', '(54) 3209-2200', 'Fernanda Lima', '2026-06-21', '2026-06-21', 'Av. Marcopolo, 280, Caxias do Sul/RS, CEP 95012-580', 25720.0, 800.0, 600.0, 25520.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 10, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 10, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 10, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 10, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'DONE', NOW(), NOW(), 10, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('4400', 'Compressor de Ar 10 Pés 140 PSI Trifásico', 'un', 2, 8900.0, 17800.0, 10);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('2211', 'Secador Frigorífico 150 l/min', 'un', 2, 3200.0, 6400.0, 10);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('0088', 'Filtro Coalescente 1" 150 l/min', 'un', 4, 380.0, 1520.0, 10);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Compressores testados e aprovados na pressão nominal.', NOW(), 46, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Sequência de produção confirmada pelo PCP.', NOW(), 47, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Carga entregue à transportadora com lacre de segurança.', NOW(), 48, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('NF autorizada pela SEFAZ-RS sem rejeições.', NOW(), 49, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Recebimento confirmado pelo setor de compras da Marcopolo.', NOW(), 50, 4);
--
---- ORDER 11
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10434, 'Pedido prioritário', 'SHIPPING', 'COMPLETED', 'Gerdau Aços Longos S.A.', '92.690.780/0001-00', '(51) 3323-2000', 'Mateus Bottega', '2026-06-01', '2026-06-01', 'Av. Farrapos, 1811, Porto Alegre/RS, CEP 90220-005', 99250.0, 1500.0, 900.0, 98650.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 11, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 11, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 11, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 11, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'DONE', NOW(), NOW(), 11, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('8820', 'Britador de Mandíbula 400x600mm', 'un', 1, 78000.0, 78000.0, 11);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('3355', 'Correia Transportadora EP400/3 B=800mm (m)', 'mt', 50, 185.0, 9250.0, 11);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('1190', 'Serviço de Montagem e Alinhamento', 'srv', 1, 12000.0, 12000.0, 11);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Britador montado e alinhado conforme especificação técnica.', NOW(), 51, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Planejamento acelerado por ser pedido prioritário.', NOW(), 52, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Transporte realizado com escolta por carga especial.', NOW(), 53, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('NF e CT-e emitidos sem pendências.', NOW(), 54, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Entrega realizada e equipamento descarregado com guindaste.', NOW(), 55, 4);
--
---- ORDER 12
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10435, 'Aguardando aprovação do cliente', 'FINAL_ASSEMBLY', 'IN_PROGRESS', 'Metalúrgica São Jorge Ltda.', '11.222.333/0001-44', '(11) 4002-8922', 'Rafael Souza', '2026-06-01', '2026-06-01', 'Rua São Jorge, 400, Santo André/SP, CEP 09111-000', 34200.0, 0.0, 180.0, 34380.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'ACTIVE', NOW(), NOW(), 12, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'WAITING', NOW(), NOW(), 12, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'WAITING', NOW(), NOW(), 12, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'WAITING', NOW(), NOW(), 12, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'WAITING', NOW(), NOW(), 12, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('2250', 'Prensa Hidráulica 30 Toneladas', 'un', 1, 22000.0, 22000.0, 12);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('6601', 'Cilindro Hidráulico 80x200mm 200 bar', 'un', 2, 1850.0, 3700.0, 12);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('4481', 'Central Hidráulica 5CV 80L 200 bar', 'un', 1, 8500.0, 8500.0, 12);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Montagem da prensa iniciada, estrutura principal posicionada.', NOW(), 56, NULL);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Aguardando aprovação do desenho revisado pelo cliente.', NOW(), 56, NULL);
--
---- ORDER 13
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10436, NULL, 'SHIPPING', 'COMPLETED', 'Indústria Bertucci S.A.', '12.345.678/0001-90', '(54) 99821-4477', 'Carla Mendes', '2026-06-30', '2026-06-30', 'Av. Industrial Anhembi, 2841 - Galpão 7, Osasco/SP, CEP 706.273-000', 18500.0, 200.0, 210.0, 18510.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 13, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 13, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 13, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 13, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'DONE', NOW(), NOW(), 13, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('5512', 'Esteira Vibratória 1200x600mm', 'un', 1, 14500.0, 14500.0, 13);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('3371', 'Vibrador Elétrico 220V 1/4CV IP55', 'un', 2, 980.0, 1960.0, 13);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('0077', 'Mola de Compressão Aço Inox D=50mm', 'un', 24, 85.0, 2040.0, 13);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Esteira montada e testada em regime contínuo por 2h.', NOW(), 61, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Materiais conferidos e sequência de produção definida.', NOW(), 62, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Coleta realizada, volume acompanhado até saída do pátio.', NOW(), 63, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('NF emitida com CFOP correto para venda interestadual.', NOW(), 64, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Entrega concluída no prazo acordado em contrato.', NOW(), 65, 4);
--
---- ORDER 14
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10437, 'Restrição de entrega no período noturno', 'FREIGHT', 'LATE', 'Confab Industrial', '98.765.432/0001-11', '(11) 98234-5678', 'Lucas Faria', '2026-08-01', '2026-08-01', 'Rua das Indústrias, 500 - Bloco B, São Paulo/SP, CEP 01310-100', 22180.0, 300.0, 390.0, 22270.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 14, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 14, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'ACTIVE', NOW(), NOW(), 14, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'WAITING', NOW(), NOW(), 14, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'WAITING', NOW(), NOW(), 14, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('7740', 'Guindaste Elétrico 2T 6m Monofásico', 'un', 1, 18500.0, 18500.0, 14);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('4460', 'Cabo de Aço 12mm 6x37 (rolo 100m)', 'rl', 2, 1200.0, 2400.0, 14);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('1180', 'Gancho de Segurança com Trava 2T', 'un', 4, 320.0, 1280.0, 14);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Guindaste montado e teste de carga realizado com êxito.', NOW(), 66, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('PCP confirmou BOM e expediu ordem de faturamento.', NOW(), 67, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Restrição de horário noturno comunicada à transportadora.', NOW(), 68, NULL);
--
---- ORDER 15
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10438, NULL, 'SHIPPING', 'COMPLETED', 'Grupo Votorantim Metais', '45.678.901/0001-23', '(19) 3211-4400', 'Fernanda Lima', '2026-08-01', '2026-08-01', 'Rodovia Anhanguera, km 110, Limeira/SP, CEP 13480-000', 40300.0, 1200.0, 750.0, 39850.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 15, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 15, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 15, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 15, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'DONE', NOW(), NOW(), 15, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('9920', 'Misturador de Pás Horizontal 500L Inox', 'un', 1, 32000.0, 32000.0, 15);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('6650', 'Redutor Helicoidal 1:20 3CV Pé/Flange', 'un', 1, 4800.0, 4800.0, 15);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('3388', 'Serviço de Polimento Interno Ra 0,8', 'srv', 1, 3500.0, 3500.0, 15);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Misturador montado e polimento interno aprovado pelo CQ.', NOW(), 71, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Planejamento encerrado, todos os insumos utilizados conferidos.', NOW(), 72, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Transporte realizado com veículo fechado e rastreado.', NOW(), 73, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('NF emitida com destaque de IPI conforme orientação fiscal.', NOW(), 74, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Entrega recebida e equipamento instalado pelo cliente.', NOW(), 75, 4);
--
---- ORDER 16
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10439, 'Conferência de volumes pendente', 'SHIPPING', 'IN_PROGRESS', 'WEG Equipamentos Elétricos', '84.429.695/0001-11', '(47) 3372-4000', 'Mateus Bottega', '2026-07-01', '2026-07-01', 'Av. Prefeito Waldemar Grubba, 3300, Jaraguá do Sul/SC, CEP 89256-900', 77500.0, 2000.0, 1100.0, 76600.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 16, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 16, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 16, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 16, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'ACTIVE', NOW(), NOW(), 16, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('1155', 'Gerador de Energia 75kVA Trifásico', 'un', 1, 58000.0, 58000.0, 16);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('8800', 'Quadro de Transferência Automática 200A', 'un', 1, 12000.0, 12000.0, 16);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('5560', 'Serviço de Instalação e Comissionamento', 'srv', 1, 7500.0, 7500.0, 16);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Gerador comissionado e operando em carga plena.', NOW(), 76, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('PCP liberou todos os itens para faturamento.', NOW(), 77, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Frete realizado com veículo prancha para carga pesada.', NOW(), 78, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('NF emitida com ICMS-ST destacado corretamente.', NOW(), 79, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Volumes conferidos, divergência de 1 volume em investigação.', NOW(), 80, NULL);
--
---- ORDER 17
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10440, NULL, 'SHIPPING', 'COMPLETED', 'Embraer Componentes S.A.', '07.689.002/0001-89', '(12) 3927-1000', 'Rafael Souza', '2026-06-21', '2026-06-21', 'Av. Brigadeiro Faria Lima, 2170, São José dos Campos/SP, CEP 12227-901', 84800.0, 3000.0, 1800.0, 83600.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 17, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 17, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 17, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 17, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'DONE', NOW(), NOW(), 17, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('3300', 'Sistema de Exaustão Industrial Q=15000 m³/h', 'un', 1, 42000.0, 42000.0, 17);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('0055', 'Duto Galvanizado Retangular 600x400mm (m)', 'mt', 80, 185.0, 14800.0, 17);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('7711', 'Filtro de Manga 200 Bags', 'un', 1, 28000.0, 28000.0, 17);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Sistema de exaustão montado e testado com anemômetro.', NOW(), 81, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Planejamento concluído com lista de corte dos dutos aprovada.', NOW(), 82, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Frete especial contratado para equipamentos frágeis.', NOW(), 83, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('NF emitida com descrição técnica detalhada dos itens.', NOW(), 84, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Entrega e instalação supervisionada pelo engenheiro de campo.', NOW(), 85, 4);
--
---- ORDER 18
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10441, 'Nota fiscal emitida', 'SHIPPING', 'COMPLETED', 'Ferbasa Ferroligas da Bahia', '15.071.387/0001-59', '(71) 3319-5000', 'Carla Mendes', '2026-06-07', '2026-06-07', 'Rodovia BA-093, km 32, Pojuca/BA, CEP 48120-000', 23120.0, 600.0, 400.0, 22920.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'DONE', NOW(), NOW(), 18, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'DONE', NOW(), NOW(), 18, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'DONE', NOW(), NOW(), 18, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'DONE', NOW(), NOW(), 18, 4);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'DONE', NOW(), NOW(), 18, 4);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('4470', 'Resfriador de Óleo a Ar 50kW', 'un', 2, 8900.0, 17800.0, 18);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('2233', 'Bomba de Engrenagem 25 l/min 150 bar', 'un', 2, 2100.0, 4200.0, 18);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('1166', 'Filtro Hidráulico Retorno 10 Micras 1"', 'un', 4, 280.0, 1120.0, 18);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Sistema hidráulico montado e testado sem vazamentos.', NOW(), 86, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('BOM encerrado com 100% dos itens consumidos conforme projeto.', NOW(), 87, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Coleta realizada pela transportadora no horário agendado.', NOW(), 88, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('NF emitida conforme observação do pedido.', NOW(), 89, 4);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Entrega concluída e comprovante de recebimento arquivado.', NOW(), 90, 4);
--
---- ORDER 19
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10442, 'Problema na linha de montagem', 'FINAL_ASSEMBLY', 'LATE', 'Companhia Siderúrgica Nacional', '33.042.730/0001-04', '(24) 3344-1000', 'Lucas Faria', '2026-08-08', '2026-08-08', 'Rod. Presidente Dutra, km 237, Volta Redonda/RJ, CEP 27255-901', 156000.0, 0.0, 700.0, 156700.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'ACTIVE', NOW(), NOW(), 19, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'WAITING', NOW(), NOW(), 19, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'WAITING', NOW(), NOW(), 19, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'WAITING', NOW(), NOW(), 19, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'WAITING', NOW(), NOW(), 19, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('6630', 'Laminador a Frio Duo 350mm', 'un', 1, 95000.0, 95000.0, 19);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('9900', 'Motor CC 50CV 220/440V c/ Encoder', 'un', 2, 18500.0, 37000.0, 19);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('4455', 'Drive CA 50CV Vetor de Fluxo', 'un', 2, 12000.0, 24000.0, 19);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Problema identificado no alinhamento dos cilindros de laminação.', NOW(), 91, NULL);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Técnico especialista acionado para correção do desvio.', NOW(), 91, NULL);
--
---- ORDER 20
--INSERT INTO tb_stepflow_order (number, observation, current_step, status, client, cnpj, phone, seller, start_date, due_date, address, subtotal, discount, shipment, total, created_at, updated_at) VALUES (10443, NULL, 'FINAL_ASSEMBLY', 'IN_PROGRESS', 'Metalúrgica São Jorge Ltda.', '11.222.333/0001-44', '(11) 4002-8922', 'Fernanda Lima', '2026-06-01', '2026-06-01', 'Rua São Jorge, 400, Santo André/SP, CEP 09111-000', 91800.0, 100.0, 150.0, 91850.0, NOW(), NOW());
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FINAL_ASSEMBLY', 'ACTIVE', NOW(), NOW(), 20, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('PCP', 'WAITING', NOW(), NOW(), 20, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('FREIGHT', 'WAITING', NOW(), NOW(), 20, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('BILLING', 'WAITING', NOW(), NOW(), 20, NULL);
--INSERT INTO tb_stepflow_order_step (step, status, started_at, finished_at, order_id, finished_by_id) VALUES ('SHIPPING', 'WAITING', NOW(), NOW(), 20, NULL);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('7750', 'Torno Mecânico CNC 500mm entre pontas', 'un', 1, 85000.0, 85000.0, 20);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('3322', 'Conjunto de Ferramentas Torneamento CNMG', 'cj', 2, 1800.0, 3600.0, 20);
--INSERT INTO tb_stepflow_order_item (item_code, description, unit, quantity, unit_price, total, order_id) VALUES ('0066', 'Serviço de Treinamento Operador CNC', 'srv', 1, 3200.0, 3200.0, 20);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Torno posicionado e nivelado na fundação preparada.', NOW(), 96, NULL);
--INSERT INTO tb_stepflow_step_message (message, created_at, step_id, created_by) VALUES ('Instalação elétrica trifásica concluída pelo eletricista.', NOW(), 96, NULL);
