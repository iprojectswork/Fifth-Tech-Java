-- C3 DDL: 数据字典（单表树 sys_dict_node） (PostgreSQL)
-- 已初始化数据库可重复执行（IF NOT EXISTS）。
-- 设计要点：
--   1. 单表自关联，parent_id=0 表示根下第一层；不存 path，运行时按链拼。
--   2. 同 parent 下未删 code 唯一，使用部分唯一索引（WHERE deleted = 0）。
--   3. 逻辑删除（deleted=0/1），索引也走部分索引过滤已删行。
--   4. 不加任何 path 列（pathCode/pathName 仅在 VO 计算）。

CREATE TABLE IF NOT EXISTS sys_dict_node (
    id            BIGINT       PRIMARY KEY,
    parent_id     BIGINT       NOT NULL DEFAULT 0,
    code          VARCHAR(64)  NOT NULL,
    name          VARCHAR(128) NOT NULL,
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

-- 未删除行：同 parent 下 code 唯一（含 / 已删行不参与唯一性，可被复用）
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_dict_node_parent_code_active
    ON sys_dict_node (parent_id, code)
    WHERE deleted = 0;

-- 按 parent_id 查直接子（懒加载 children）
CREATE INDEX IF NOT EXISTS idx_sys_dict_node_parent
    ON sys_dict_node (parent_id)
    WHERE deleted = 0;

-- 业务 /dict/data 按 status 过滤
CREATE INDEX IF NOT EXISTS idx_sys_dict_node_status
    ON sys_dict_node (status)
    WHERE deleted = 0;

-- ----------------------------------------------------------------------------
-- 可选演示数据（§4.3 样例树）：验收时便于操作；正式环境可注释掉。
-- 仅在表内无任何数据时插入（id 写死方便人工跑通；生产请改雪花 ID）。
-- ----------------------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM sys_dict_node) THEN
        INSERT INTO sys_dict_node (id, parent_id, code, name, sort, status) VALUES
            (1001, 0,    'gender',  '性别',     1, 1),
            (1002, 1001, 'M',       '男',       1, 1),
            (1003, 1001, 'F',       '女',       2, 1),
            (1004, 0,    'system',  '系统',     2, 1),
            (1005, 1004, 'user',    '用户',     1, 1),
            (1006, 1005, 'status',  '状态',     1, 1),
            (1007, 1006, '1',       '启用',     1, 1),
            (1008, 1006, '0',       '禁用',     2, 1);
    END IF;
END $$;