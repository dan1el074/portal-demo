ALTER TABLE tb_role
    ADD COLUMN activated BOOLEAN DEFAULT TRUE NOT NULL;

INSERT INTO tb_role (authority, title, parent, father_id)
    SELECT 'ROLE_STEP_FLOW_ADMIN', 'Administrador', parent_role.parent, parent_role.id
    FROM tb_role parent_role
    WHERE parent_role.authority = 'ROLE_STEP_FLOW'
      AND NOT EXISTS (SELECT 1 FROM tb_role WHERE authority = 'ROLE_STEP_FLOW_ADMIN');

INSERT INTO tb_role (authority, title, parent, father_id)
    SELECT 'ROLE_STEP_FLOW_OPERATOR', 'Operador', parent_role.parent, parent_role.id
    FROM tb_role parent_role
    WHERE parent_role.authority = 'ROLE_STEP_FLOW'
      AND NOT EXISTS (SELECT 1 FROM tb_role WHERE authority = 'ROLE_STEP_FLOW_OPERATOR');

INSERT INTO tb_role (authority, title, parent, father_id)
    SELECT 'ROLE_STEP_FLOW_CONSULTATION', 'Consulta', parent_role.parent, parent_role.id
    FROM tb_role parent_role
    WHERE parent_role.authority = 'ROLE_STEP_FLOW'
      AND NOT EXISTS (SELECT 1 FROM tb_role WHERE authority = 'ROLE_STEP_FLOW_CONSULTATION');

UPDATE tb_role
SET title = CASE authority
        WHEN 'ROLE_STEP_FLOW_ADMIN' THEN 'Administrador'
        WHEN 'ROLE_STEP_FLOW_OPERATOR' THEN 'Operador'
        WHEN 'ROLE_STEP_FLOW_CONSULTATION' THEN 'Consulta'
    END,
    parent = (SELECT parent_role.parent FROM tb_role parent_role WHERE parent_role.authority = 'ROLE_STEP_FLOW'),
    father_id = (SELECT parent_role.id FROM tb_role parent_role WHERE parent_role.authority = 'ROLE_STEP_FLOW')
WHERE authority IN (
    'ROLE_STEP_FLOW_ADMIN',
    'ROLE_STEP_FLOW_OPERATOR',
    'ROLE_STEP_FLOW_CONSULTATION'
)
  AND EXISTS (SELECT 1 FROM tb_role parent_role WHERE parent_role.authority = 'ROLE_STEP_FLOW');

UPDATE tb_role
SET activated = CASE
    WHEN authority = 'ROLE_STEP_FLOW_ADMIN' THEN FALSE
    ELSE TRUE
END;

INSERT INTO tb_user_role (user_id, role_id)
    SELECT user_admin.user_id, consultation_role.id
    FROM tb_user_role user_admin
    JOIN tb_role admin_role
      ON admin_role.id = user_admin.role_id
     AND admin_role.authority = 'ROLE_STEP_FLOW_ADMIN'
    JOIN tb_role consultation_role
      ON consultation_role.authority = 'ROLE_STEP_FLOW_CONSULTATION'
    WHERE NOT EXISTS (
        SELECT 1
        FROM tb_user_role existing_consultation
        WHERE existing_consultation.user_id = user_admin.user_id
          AND existing_consultation.role_id = consultation_role.id
    );

DELETE FROM tb_user_role
WHERE role_id = (
    SELECT admin_role.id
    FROM tb_role admin_role
    WHERE admin_role.authority = 'ROLE_STEP_FLOW_ADMIN'
);

INSERT INTO tb_user_role (user_id, role_id)
    SELECT user_step_flow.user_id, operator_role.id
    FROM tb_user_role user_step_flow
    JOIN tb_role step_flow_role
      ON step_flow_role.id = user_step_flow.role_id
     AND step_flow_role.authority = 'ROLE_STEP_FLOW'
    JOIN tb_role operator_role
      ON operator_role.authority = 'ROLE_STEP_FLOW_OPERATOR'
    WHERE NOT EXISTS (
        SELECT 1
        FROM tb_user_role user_access_level
        JOIN tb_role access_level_role ON access_level_role.id = user_access_level.role_id
        WHERE user_access_level.user_id = user_step_flow.user_id
          AND access_level_role.authority IN (
              'ROLE_STEP_FLOW_ADMIN',
              'ROLE_STEP_FLOW_OPERATOR',
              'ROLE_STEP_FLOW_CONSULTATION'
          )
    );

INSERT INTO tb_user_role (user_id, role_id)
WITH RECURSIVE missing_ancestor_roles (user_id, role_id) AS (
    SELECT user_role.user_id, assigned_role.father_id
    FROM tb_user_role user_role
    JOIN tb_role assigned_role ON assigned_role.id = user_role.role_id
    WHERE assigned_role.father_id IS NOT NULL

    UNION

    SELECT missing_ancestor.user_id, ancestor_role.father_id
    FROM missing_ancestor_roles missing_ancestor
    JOIN tb_role ancestor_role ON ancestor_role.id = missing_ancestor.role_id
    WHERE ancestor_role.father_id IS NOT NULL
)
    SELECT missing_ancestor.user_id, missing_ancestor.role_id
    FROM missing_ancestor_roles missing_ancestor
    WHERE NOT EXISTS (
        SELECT 1
        FROM tb_user_role existing_user_role
        WHERE existing_user_role.user_id = missing_ancestor.user_id
          AND existing_user_role.role_id = missing_ancestor.role_id
    );
