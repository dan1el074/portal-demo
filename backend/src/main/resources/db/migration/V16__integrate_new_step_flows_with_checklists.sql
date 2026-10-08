ALTER TABLE tb_stepflow_order ADD COLUMN checklist_integration BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tb_stepflow_order_step ADD COLUMN checklist_pending BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tb_stepflow_order ALTER COLUMN checklist_integration SET DEFAULT TRUE;
