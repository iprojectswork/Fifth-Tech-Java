-- B2 对齐：列表 component 逻辑 key + list/add/edit/view/delete 分码
-- C4：组织与角色解耦；需先有默认组织 HQ id=1（见 org.sql）
-- 清空表数据
DELETE FROM sys_role_permission;
DELETE FROM sys_user_role;
DELETE FROM sys_user_org;
DELETE FROM sys_permission;
DELETE FROM sys_role;

-- 默认组织（幂等）
INSERT INTO sys_org (id, parent_id, org_code, org_name, org_type, sort, status)
VALUES (1, 0, 'HQ', '总公司', 'company', 1, 1)
ON CONFLICT (id) DO UPDATE SET
    org_code = EXCLUDED.org_code,
    org_name = EXCLUDED.org_name,
    org_type = EXCLUDED.org_type,
    deleted = 0;

-- 重置序列（若使用序列；雪花 ID 环境可忽略失败）
-- ALTER SEQUENCE sys_role_id_seq RESTART WITH 1;
-- ALTER SEQUENCE sys_permission_id_seq RESTART WITH 1;

-- 插入角色数据（全局角色，不挂组织）
INSERT INTO sys_role (id, role_name, role_code, description, sort, status) VALUES
(1, '超级管理员', 'super_admin', '拥有所有权限', 1, 1),
(2, '管理员', 'admin', '拥有管理权限', 2, 1),
(3, '普通用户', 'user', '基础查看权限', 3, 1);

-- 目录 + 列表菜单（permission_type=1）
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(1, '系统管理', 'system', 1, 0, '', '', 'Setting', 1, 1),
(2, '用户管理', 'system:user:list', 1, 1, '/system/user/list', 'user/list', 'User', 1, 1),
(3, '角色管理', 'system:role:list', 1, 1, '/system/role/list', 'system/role/list', 'UserFilled', 2, 1),
(4, '权限管理', 'system:permission:list', 1, 1, '/system/permission/list', 'system/permission/list', 'Lock', 3, 1);

-- 用户按钮
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(101, '用户新增', 'system:user:add', 2, 2, NULL, NULL, NULL, 1, 1),
(102, '用户编辑', 'system:user:edit', 2, 2, NULL, NULL, NULL, 2, 1),
(103, '用户查看', 'system:user:view', 2, 2, NULL, NULL, NULL, 3, 1),
(104, '用户删除', 'system:user:delete', 2, 2, NULL, NULL, NULL, 4, 1);

-- 角色按钮
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(201, '角色新增', 'system:role:add', 2, 3, NULL, NULL, NULL, 1, 1),
(202, '角色编辑', 'system:role:edit', 2, 3, NULL, NULL, NULL, 2, 1),
(203, '角色查看', 'system:role:view', 2, 3, NULL, NULL, NULL, 3, 1),
(204, '角色删除', 'system:role:delete', 2, 3, NULL, NULL, NULL, 4, 1);

-- 权限按钮
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(301, '权限新增', 'system:permission:add', 2, 4, NULL, NULL, NULL, 1, 1),
(302, '权限编辑', 'system:permission:edit', 2, 4, NULL, NULL, NULL, 2, 1),
(303, '权限查看', 'system:permission:view', 2, 4, NULL, NULL, NULL, 3, 1),
(304, '权限删除', 'system:permission:delete', 2, 4, NULL, NULL, NULL, 4, 1);

-- C1 缓存管理菜单（parent_id=1 系统管理）
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(5, '缓存管理', 'system:cache:list', 1, 1, '/system/cache/list', 'system/cache/list', 'Coin', 4, 1);

-- C1 缓存管理按钮（parent_id=5）
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(401, '缓存新增', 'system:cache:add', 2, 5, NULL, NULL, NULL, 1, 1),
(402, '缓存编辑', 'system:cache:edit', 2, 5, NULL, NULL, NULL, 2, 1),
(403, '缓存查看', 'system:cache:view', 2, 5, NULL, NULL, NULL, 3, 1),
(404, '缓存删除', 'system:cache:delete', 2, 5, NULL, NULL, NULL, 4, 1);

-- C2 编码管理菜单（parent_id=1 系统管理）
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(6, '编码管理', 'system:code:list', 1, 1, '/system/code/list', 'system/code/list', 'PriceTag', 5, 1);

-- C2 编码管理按钮（parent_id=6）
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(501, '编码新增', 'system:code:add', 2, 6, NULL, NULL, NULL, 1, 1),
(502, '编码编辑', 'system:code:edit', 2, 6, NULL, NULL, NULL, 2, 1),
(503, '编码查看', 'system:code:view', 2, 6, NULL, NULL, NULL, 3, 1),
(504, '编码删除', 'system:code:delete', 2, 6, NULL, NULL, NULL, 4, 1);

-- C3 数据字典菜单（parent_id=1 系统管理）
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(7, '数据字典', 'system:dict:list', 1, 1, '/system/dict/list', 'system/dict/list', 'Notebook', 6, 1);

-- C3 数据字典按钮（parent_id=7）
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(601, '字典新增', 'system:dict:add', 2, 7, NULL, NULL, NULL, 1, 1),
(602, '字典编辑', 'system:dict:edit', 2, 7, NULL, NULL, NULL, 2, 1),
(603, '字典查看', 'system:dict:view', 2, 7, NULL, NULL, NULL, 3, 1),
(604, '字典删除', 'system:dict:delete', 2, 7, NULL, NULL, NULL, 4, 1);

-- C4 组织管理菜单（parent_id=1 系统管理）
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(8, '组织管理', 'system:org:list', 1, 1, '/system/org/list', 'system/org/list', 'OfficeBuilding', 7, 1);

-- C4 组织管理按钮（parent_id=8）
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status) VALUES
(701, '组织新增', 'system:org:add',    2, 8, NULL, NULL, NULL, 1, 1),
(702, '组织编辑', 'system:org:edit',   2, 8, NULL, NULL, NULL, 2, 1),
(703, '组织查看', 'system:org:view',   2, 8, NULL, NULL, NULL, 3, 1),
(704, '组织删除', 'system:org:delete', 2, 8, NULL, NULL, NULL, 4, 1);

INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT id, 1, id FROM sys_permission;

-- 给默认用户分配超级管理员角色 + 默认组织（user_id=1）
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(1, 1, 1)
ON CONFLICT DO NOTHING;

INSERT INTO sys_user_org (id, user_id, org_id, create_time) VALUES
(1, 1, 1, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
