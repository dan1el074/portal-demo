INSERT INTO tb_role (authority, title, parent, father_id)
SELECT 'ROLE_RAW_MATERIALS_ADMIN', 'Administrador', parent.parent, parent.id
FROM tb_role parent
WHERE parent.authority = 'ROLE_RAW_MATERIALS'
  AND NOT EXISTS (
    SELECT 1 FROM tb_role WHERE authority = 'ROLE_RAW_MATERIALS_ADMIN'
  );

INSERT INTO tb_role (authority, title, parent, father_id)
SELECT 'ROLE_RAW_MATERIALS_OPERATOR', 'Operador', parent.parent, parent.id
FROM tb_role parent
WHERE parent.authority = 'ROLE_RAW_MATERIALS'
  AND NOT EXISTS (
    SELECT 1 FROM tb_role WHERE authority = 'ROLE_RAW_MATERIALS_OPERATOR'
  );

INSERT INTO tb_role (authority, title, parent, father_id)
SELECT 'ROLE_RAW_MATERIALS_CONSULTATION', 'Consulta', parent.parent, parent.id
FROM tb_role parent
WHERE parent.authority = 'ROLE_RAW_MATERIALS'
  AND NOT EXISTS (
    SELECT 1 FROM tb_role WHERE authority = 'ROLE_RAW_MATERIALS_CONSULTATION'
  );
