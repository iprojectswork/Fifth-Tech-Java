-- 删除旧数据
DELETE FROM sys_role_permission;
DELETE FROM sys_user_role;
DELETE FROM sys_permission;
DELETE FROM sys_role;

-- 重新插入角色数据
INSERT INTO sys_role (id, role_name, role_code, description, sort) VALUES
(1, '超级管理员', 'super_admin', '拥有所有权限', 1),
(2, '管理员', 'admin', '拥有管理权限', 2),
(3, '普通用户', 'user', '基础查看权限', 3);

-- 重新插入权限数据 - 菜单权限
-- 系统管理是目录，path为空，点击时跳转到第一个子菜单
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort) VALUES
(1, '系统管理', 'system', 1, 0, '', '', 'Setting', 1),
(2, '用户管理', 'user:view', 1, 1, '/system/user/list', 'views/user/list', 'User', 1),
(3, '角色管理', 'role:view', 1, 1, '/system/role/list', 'views/role/list', 'UserFilled', 2),
(4, '权限管理', 'permission:view', 1, 1, '/system/permission/list', 'views/permission/list', 'Lock', 3);

-- 重新插入权限数据 - 按钮权限
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, sort) VALUES
(101, '用户新增', 'user:create', 2, 2, 1),
(102, '用户编辑', 'user:update', 2, 2, 2),
(103, '用户删除', 'user:delete', 2, 2, 3),
(201, '角色新增', 'role:create', 2, 3, 1),
(202, '角色编辑', 'role:update', 2, 3, 2),
(203, '角色删除', 'role:delete', 2, 3, 3),
(301, '权限新增', 'permission:create', 2, 4, 1),
(302, '权限编辑', 'permission:update', 2, 4, 2),
(303, '权限删除', 'permission:delete', 2, 4, 3);

-- 超级管理员拥有所有权限
INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT nextval('sys_role_permission_id_seq'), 1, id FROM sys_permission;

-- 给默认用户分配超级管理员角色
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(nextval('sys_user_role_id_seq'), 1, 1);
