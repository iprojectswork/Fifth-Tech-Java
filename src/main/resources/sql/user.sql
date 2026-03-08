-- 创建用户表
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    status INTEGER DEFAULT 0 COMMENT '状态（0：未提交，1：已提交，2：已审核，10：审批中）',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '是否删除（0：否，1：是）',
    delete_time TIMESTAMP COMMENT '删除时间'
);

-- 创建索引
CREATE INDEX idx_username ON sys_user(username);
CREATE INDEX idx_email ON sys_user(email);
CREATE INDEX idx_phone ON sys_user(phone);
CREATE INDEX idx_status ON sys_user(status);

-- 插入测试数据
INSERT INTO sys_user (id, username, password, nickname, email, phone, status) VALUES
(1, 'admin', '123456', '管理员', 'admin@example.com', '13800138000', 2),
(2, 'zhangsan', '123456', '张三', 'zhangsan@example.com', '13800138001', 0),
(3, 'lisi', '123456', '李四', 'lisi@example.com', '13800138002', 1),
(4, 'wangwu', '123456', '王五', 'wangwu@example.com', '13800138003', 2),
(5, 'zhaoliu', '123456', '赵六', 'zhaoliu@example.com', '13800138004', 10),
(6, 'sunqi', '123456', '孙七', 'sunqi@example.com', '13800138005', 0),
(7, 'zhouba', '123456', '周八', 'zhouba@example.com', '13800138006', 1),
(8, 'wujiu', '123456', '吴九', 'wujiu@example.com', '13800138007', 2),
(9, 'zhengshi', '123456', '郑十', 'zhengshi@example.com', '13800138008', 10);
