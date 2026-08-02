-- C2 增量种子：编码管理菜单 + 按钮 + 绑定 super_admin(role_id=1)
-- 已初始化数据库可重复执行，不影响其它数据。
-- PostgreSQL 语法（ON CONFLICT DO NOTHING）。
-- 菜单/按钮 ID 与 cache_permission 同段位推后：menu=6, buttons=501~504。

-- 1) 编码管理菜单（parent_id=1 系统管理）
INSERT INTO sys_permission
    (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status)
VALUES
    (6, '编码管理', 'system:code:list', 1, 1, '/system/code/list', 'system/code/list', 'PriceTag', 5, 1)
ON CONFLICT (id) DO NOTHING;

-- 2) 编码管理按钮（parent_id=6）
INSERT INTO sys_permission
    (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status)
VALUES
    (501, '编码新增', 'system:code:add',    2, 6, NULL, NULL, NULL, 1, 1),
    (502, '编码编辑', 'system:code:edit',   2, 6, NULL, NULL, NULL, 2, 1),
    (503, '编码查看', 'system:code:view',   2, 6, NULL, NULL, NULL, 3, 1),
    (504, '编码删除', 'system:code:delete', 2, 6, NULL, NULL, NULL, 4, 1)
ON CONFLICT (id) DO NOTHING;

-- 3) 绑定到 super_admin（role_id=1）
-- id 与 permission_id 对齐（同 init_rbac）；UK (role_id, permission_id)
INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT p.id, 1, p.id
FROM sys_permission p
WHERE p.id IN (6, 501, 502, 503, 504)
ON CONFLICT (role_id, permission_id) DO NOTHING;
