-- 角色与组织解耦，组织类型改为字典 code
-- 已落地库可重复执行（PostgreSQL）

-- 1) 去掉按组织重复的演示角色，只留全局角色
UPDATE sys_user_role ur
SET role_id = keep.id
FROM sys_role victim
JOIN LATERAL (
    SELECT r.id
    FROM sys_role r
    WHERE r.deleted = 0
      AND r.role_code = victim.role_code
    ORDER BY r.id
    LIMIT 1
) keep ON TRUE
WHERE ur.role_id = victim.id
  AND victim.deleted = 0
  AND victim.id <> keep.id;

UPDATE sys_role victim
SET deleted = 1,
    delete_time = CURRENT_TIMESTAMP
FROM (
    SELECT r.id
    FROM sys_role r
    WHERE r.deleted = 0
      AND EXISTS (
          SELECT 1
          FROM sys_role keep
          WHERE keep.deleted = 0
            AND keep.role_code = r.role_code
            AND keep.id < r.id
      )
) dup
WHERE victim.id = dup.id;

DROP INDEX IF EXISTS uk_sys_role_org_code_active;
DROP INDEX IF EXISTS idx_sys_role_org;

ALTER TABLE sys_role DROP COLUMN IF EXISTS org_id;

CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_role_code_active
    ON sys_role (role_code)
    WHERE deleted = 0;

-- 2) org_type：SMALLINT 1/2/3 → 字典 code
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'sys_org'
          AND column_name = 'org_type'
          AND data_type IN ('smallint', 'integer', 'bigint')
    ) THEN
        ALTER TABLE sys_org
            ALTER COLUMN org_type TYPE VARCHAR(64)
            USING (
                CASE org_type::text
                    WHEN '1' THEN 'company'
                    WHEN '2' THEN 'dept'
                    WHEN '3' THEN 'group'
                    ELSE org_type::text
                END
            );
    END IF;
END $$;

UPDATE sys_org
SET org_type = CASE org_type
    WHEN '1' THEN 'company'
    WHEN '2' THEN 'dept'
    WHEN '3' THEN 'group'
    ELSE org_type
END
WHERE org_type IN ('1', '2', '3');
