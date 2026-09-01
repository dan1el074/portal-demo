INSERT INTO tb_role (authority, title, title_url, parent, parent_url, activated)
SELECT 'ROLE_SYSTEM_PARAMS', 'Parâmetros', '/params', 'Sistema', '/system', TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM tb_role
    WHERE authority = 'ROLE_SYSTEM_PARAMS'
);
