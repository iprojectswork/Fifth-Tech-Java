-- 缓存管理菜单 + 按钮 + 绑定 super_admin(role_id=1)
-- 已初始化数据库可重复执行，不影响其它数据。
-- PostgreSQL 语法（ON CONFLICT DO NOTHING）。

-- 1) 缓存管理菜单（parent_id=1 系统管理）
INSERT INTO sys_permission
    (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status)
VALUES
    (5, '缓存管理', 'system:cache:list', 1, 1, '/system/cache/list', 'system/cache/list', 'Coin', 4, 1)
ON CONFLICT (id) DO NOTHING;

-- 2) 缓存管理按钮（parent_id=5）
INSERT INTO sys_permission
    (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status)
VALUES
    (401, '缓存新增', 'system:cache:add',    2, 5, NULL, NULL, NULL, 1, 1),
    (402, '缓存编辑', 'system:cache:edit',   2, 5, NULL, NULL, NULL, 2, 1),
    (403, '缓存查看', 'system:cache:view',   2, 5, NULL, NULL, NULL, 3, 1),
    (404, '缓存删除', 'system:cache:delete', 2, 5, NULL, NULL, NULL, 4, 1)
ON CONFLICT (id) DO NOTHING;

-- 3) 绑定到 super_admin（role_id=1）
-- id 与 permission_id 对齐（同 init_rbac）；UK (role_id, permission_id)
INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT p.id, 1, p.id
FROM sys_permission p
WHERE p.id IN (5, 401, 402, 403, 404)
ON CONFLICT (role_id, permission_id) DO NOTHING;
