-- C4 DDL: 组织单元 sys_org + 用户挂靠 sys_user_org (PostgreSQL)
-- 可重复执行（IF NOT EXISTS）。
-- 设计：单表树 parent_id=0 为根下第一层；path 不落库；org_type 存字典 org/type 的 code。

CREATE TABLE IF NOT EXISTS sys_org (
    id            BIGINT       PRIMARY KEY,
    parent_id     BIGINT       NOT NULL DEFAULT 0,
    org_code      VARCHAR(64)  NOT NULL,
    org_name      VARCHAR(128) NOT NULL,
    org_type      VARCHAR(64)  NOT NULL,
    sort          INTEGER      NOT NULL DEFAULT 0,
    status        SMALLINT     NOT NULL DEFAULT 1,
    remark        VARCHAR(512),
    create_id     BIGINT,
    create_name   VARCHAR(50),
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_id     BIGINT,
    update_name   VARCHAR(50),
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       INTEGER      NOT NULL DEFAULT 0,
    delete_id     BIGINT,
    delete_name   VARCHAR(50),
    delete_time   TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_org_parent_code_active
    ON sys_org (parent_id, org_code)
    WHERE deleted = 0;

CREATE INDEX IF NOT EXISTS idx_sys_org_parent
    ON sys_org (parent_id)
    WHERE deleted = 0;

CREATE INDEX IF NOT EXISTS idx_sys_org_status
    ON sys_org (status)
    WHERE deleted = 0;

CREATE INDEX IF NOT EXISTS idx_sys_org_type
    ON sys_org (org_type)
    WHERE deleted = 0;

CREATE TABLE IF NOT EXISTS sys_user_org (
    id          BIGINT    PRIMARY KEY,
    user_id     BIGINT    NOT NULL,
    org_id      BIGINT    NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_user_org_user_org
    ON sys_user_org (user_id, org_id);

CREATE INDEX IF NOT EXISTS idx_sys_user_org_org
    ON sys_user_org (org_id);

CREATE INDEX IF NOT EXISTS idx_sys_user_org_user
    ON sys_user_org (user_id);

-- 默认根组织 HQ（固定 id=1，供角色迁移与 init_rbac 引用）
-- 演示树：仅当仅有 HQ 或空表时可选插入
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM sys_org WHERE id = 1) THEN
        INSERT INTO sys_org (id, parent_id, org_code, org_name, org_type, sort, status)
        VALUES (1, 0, 'HQ', '总公司', 'company', 1, 1);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM sys_org WHERE id <> 1 AND deleted = 0) THEN
        INSERT INTO sys_org (id, parent_id, org_code, org_name, org_type, sort, status) VALUES
            (2, 1, 'RD', '研发中心', 'dept', 1, 1),
            (3, 2, 'FE', '前端组', 'group', 1, 1),
            (4, 2, 'BE', '后端组', 'group', 2, 1),
            (5, 1, 'QA', '质量部', 'dept', 2, 1)
        ON CONFLICT (id) DO NOTHING;
    END IF;
END $$;
