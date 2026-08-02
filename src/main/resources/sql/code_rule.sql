-- C2 DDL: 编码规则 + 流水水位 (PostgreSQL)
-- 已初始化数据库可重复执行（IF NOT EXISTS）。
-- segments_json 用 JSONB 保存；rule_code 全局唯一；(rule_id, period_key) 唯一。

CREATE TABLE IF NOT EXISTS sys_code_rule (
    id            BIGINT       PRIMARY KEY,
    rule_code     VARCHAR(64)  NOT NULL UNIQUE,
    rule_name     VARCHAR(128) NOT NULL,
    segments_json JSONB        NOT NULL,
    batch_size    INTEGER      NOT NULL DEFAULT 100,
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

CREATE INDEX IF NOT EXISTS idx_sys_code_rule_status ON sys_code_rule(status);
CREATE INDEX IF NOT EXISTS idx_sys_code_rule_deleted ON sys_code_rule(deleted);

CREATE TABLE IF NOT EXISTS sys_code_sequence (
    id           BIGINT      PRIMARY KEY,
    rule_code    VARCHAR(64) NOT NULL,
    rule_id      BIGINT      NOT NULL,
    period_key   VARCHAR(32) NOT NULL,
    current_max  BIGINT      NOT NULL DEFAULT 0,
    create_time  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_code_sequence UNIQUE (rule_id, period_key)
);

CREATE INDEX IF NOT EXISTS idx_sys_code_sequence_rule_code ON sys_code_sequence(rule_code);
