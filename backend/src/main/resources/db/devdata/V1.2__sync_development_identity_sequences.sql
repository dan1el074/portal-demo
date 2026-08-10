-- Os dados de demonstração usam IDs explícitos. No H2, esses inserts não
-- avançam automaticamente o próximo valor das colunas IDENTITY.
ALTER TABLE tb_param ALTER COLUMN id RESTART WITH 2;
ALTER TABLE tb_position ALTER COLUMN id RESTART WITH 20;
ALTER TABLE tb_file ALTER COLUMN id RESTART WITH 4;
ALTER TABLE tb_picture ALTER COLUMN id RESTART WITH 20;
