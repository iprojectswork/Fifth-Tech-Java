-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    status INTEGER DEFAULT 1,
    sort INTEGER DEFAULT 0,
    create_id BIGINT,
    create_name VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_id BIGINT,
    update_name VARCHAR(50),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    delete_id BIGINT,
    delete_name VARCHAR(50),
    delete_time TIMESTAMP
);

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY,
    permission_name VARCHAR(50) NOT NULL,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    permission_type INTEGER NOT NULL,
    parent_id BIGINT DEFAULT 0,
    path VARCHAR(255),
    component VARCHAR(255),
    icon VARCHAR(100),
    status INTEGER DEFAULT 1,
    sort INTEGER DEFAULT 0,
    create_id BIGINT,
    create_name VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_id BIGINT,
    update_name VARCHAR(50),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    delete_id BIGINT,
    delete_name VARCHAR(50),
    delete_time TIMESTAMP
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id)
);

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, permission_id)
);

-- 初始角色数据
INSERT INTO sys_role (id, role_name, role_code, description, sort) VALUES
(1, '超级管理员', 'super_admin', '拥有所有权限', 1),
(2, '管理员', 'admin', '拥有管理权限', 2),
(3, '普通用户', 'user', '基础查看权限', 3)
ON CONFLICT (id) DO NOTHING;

-- 初始权限数据 - 菜单权限
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort) VALUES
(1, '系统管理', 'system', 1, 0, '/system', 'Layout', 'Setting', 1),
(2, '用户管理', 'user:view', 1, 1, '/system/user', 'views/user/list', 'User', 1),
(3, '角色管理', 'role:view', 1, 1, '/system/role', 'views/role/list', 'UserFilled', 2),
(4, '权限管理', 'permission:view', 1, 1, '/system/permission', 'views/permission/list', 'Lock', 3)
ON CONFLICT (id) DO NOTHING;

-- 初始权限数据 - 按钮权限
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, sort) VALUES
(101, '用户新增', 'user:create', 2, 2, 1),
(102, '用户编辑', 'user:update', 2, 2, 2),
(103, '用户删除', 'user:delete', 2, 2, 3),
(201, '角色新增', 'role:create', 2, 3, 1),
(202, '角色编辑', 'role:update', 2, 3, 2),
(203, '角色删除', 'role:delete', 2, 3, 3),
(301, '权限新增', 'permission:create', 2, 4, 1),
(302, '权限编辑', 'permission:update', 2, 4, 2),
(303, '权限删除', 'permission:delete', 2, 4, 3)
ON CONFLICT (id) DO NOTHING;

-- 超级管理员拥有所有权限
INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT nextval('sys_role_permission_id_seq'), 1, id FROM sys_permission
ON CONFLICT DO NOTHING;

-- 给默认用户分配超级管理员角色
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(nextval('sys_user_role_id_seq'), 1, 1)
ON CONFLICT DO NOTHING;
