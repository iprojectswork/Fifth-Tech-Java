-- 存量迁移：sys_role.org_id + 索引 + 演示种子
-- 前置：org.sql（至少有 HQ id=1）
-- PostgreSQL，可重复执行

INSERT INTO sys_org (id, parent_id, org_code, org_name, org_type, sort, status)
VALUES (1, 0, 'HQ', '总公司', 1, 1, 1)
ON CONFLICT (id) DO NOTHING;

-- 新列默认 1：首次 ADD 时 PG 会给已有行写入 1（无需再 UPDATE）
ALTER TABLE sys_role ADD COLUMN IF NOT EXISTS org_id BIGINT DEFAULT 1;

DO $$
BEGIN
    ALTER TABLE sys_role DROP CONSTRAINT IF EXISTS sys_role_role_code_key;
EXCEPTION WHEN OTHERS THEN
    NULL;
END $$;

DO $$
BEGIN
    ALTER TABLE sys_role DROP CONSTRAINT IF EXISTS sys_role_role_name_key;
EXCEPTION WHEN OTHERS THEN
    NULL;
END $$;

DROP INDEX IF EXISTS uk_role_code;
DROP INDEX IF EXISTS idx_role_code;
DROP INDEX IF EXISTS sys_role_role_code_key;

CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_role_org_code_active
    ON sys_role (org_id, role_code)
    WHERE deleted = 0;

CREATE INDEX IF NOT EXISTS idx_sys_role_org
    ON sys_role (org_id)
    WHERE deleted = 0;

DO $$
BEGIN
    ALTER TABLE sys_role ALTER COLUMN org_id SET NOT NULL;
EXCEPTION WHEN OTHERS THEN
    NULL;
END $$;

DO $$
BEGIN
    ALTER TABLE sys_role ALTER COLUMN org_id DROP DEFAULT;
EXCEPTION WHEN OTHERS THEN
    NULL;
END $$;

-- 演示角色 / 挂靠（靠主键冲突幂等，不用 WHERE 判空）
INSERT INTO sys_role (id, org_id, role_name, role_code, description, sort, status)
VALUES
    (101, 2, '部门主管', 'dept_leader', '研发中心主管', 10, 1),
    (102, 5, '部门主管', 'dept_leader', '质量部主管', 10, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_user_org (id, user_id, org_id, create_time)
VALUES (1, 1, 1, CURRENT_TIMESTAMP)
ON CONFLICT (user_id, org_id) DO NOTHING;
