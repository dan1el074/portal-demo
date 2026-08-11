UPDATE tb_position SET is_locked = FALSE WHERE is_locked IS NULL;

ALTER TABLE tb_position ALTER COLUMN is_locked SET DEFAULT FALSE;
ALTER TABLE tb_position ALTER COLUMN is_locked SET NOT NULL;
