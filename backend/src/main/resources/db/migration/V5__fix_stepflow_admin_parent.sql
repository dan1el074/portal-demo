UPDATE tb_role
SET father_id = (
    SELECT parent_role.id
    FROM tb_role parent_role
    WHERE parent_role.authority = 'ROLE_STEP_FLOW'
)
WHERE authority = 'ROLE_STEP_FLOW_ADMIN'
  AND EXISTS (
    SELECT 1
    FROM tb_role parent_role
    WHERE parent_role.authority = 'ROLE_STEP_FLOW'
  )
  AND (
    father_id IS NULL
    OR father_id <> (
      SELECT parent_role.id
      FROM tb_role parent_role
      WHERE parent_role.authority = 'ROLE_STEP_FLOW'
    )
  );
