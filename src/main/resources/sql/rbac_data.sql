-- 创建序列
CREATE SEQUENCE IF NOT EXISTS sys_user_role_id_seq;
CREATE SEQUENCE IF NOT EXISTS sys_role_permission_id_seq;

-- 初始角色数据
INSERT INTO sys_role (id, role_name, role_code, description, sort) VALUES
(1, 'super_admin_role', 'super_admin', 'all permissions', 1),
(2, 'admin_role', 'admin', 'admin permissions', 2),
(3, 'user_role', 'user', 'basic permissions', 3)
ON CONFLICT (id) DO NOTHING;

-- 初始权限数据 - 菜单权限
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort) VALUES
(1, 'system', 'system', 1, 0, '/system', 'Layout', 'Setting', 1),
(2, 'user_view', 'user:view', 1, 1, '/system/user', 'views/user/list', 'User', 1),
(3, 'role_view', 'role:view', 1, 1, '/system/role', 'views/role/list', 'UserFilled', 2),
(4, 'permission_view', 'permission:view', 1, 1, '/system/permission', 'views/permission/list', 'Lock', 3)
ON CONFLICT (id) DO NOTHING;

-- 初始权限数据 - 按钮权限
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, sort) VALUES
(101, 'user_create', 'user:create', 2, 2, 1),
(102, 'user_update', 'user:update', 2, 2, 2),
(103, 'user_delete', 'user:delete', 2, 2, 3),
(201, 'role_create', 'role:create', 2, 3, 1),
(202, 'role_update', 'role:update', 2, 3, 2),
(203, 'role_delete', 'role:delete', 2, 3, 3),
(301, 'permission_create', 'permission:create', 2, 4, 1),
(302, 'permission_update', 'permission:update', 2, 4, 2),
(303, 'permission_delete', 'permission:delete', 2, 4, 3)
ON CONFLICT (id) DO NOTHING;

-- 超级管理员拥有所有权限
INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT nextval('sys_role_permission_id_seq'), 1, id FROM sys_permission
ON CONFLICT DO NOTHING;

-- 给默认用户分配超级管理员角色
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(nextval('sys_user_role_id_seq'), 1, 1)
ON CONFLICT DO NOTHING;
