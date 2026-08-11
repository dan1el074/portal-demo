ALTER TABLE tb_stepflow_order ADD COLUMN occurrence INTEGER;

UPDATE tb_stepflow_order current_order
SET occurrence = ranked_order.occurrence
FROM (
    SELECT id, ROW_NUMBER() OVER (PARTITION BY number ORDER BY id) AS occurrence FROM tb_stepflow_order
) ranked_order
WHERE current_order.id = ranked_order.id;

ALTER TABLE tb_stepflow_order ALTER COLUMN occurrence SET NOT NULL;

ALTER TABLE tb_stepflow_order ADD CONSTRAINT uk_stepflow_order_number_occurrence UNIQUE (number, occurrence);
