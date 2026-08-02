-- C3 增量种子：数据字典菜单 + 按钮 + 绑定 super_admin(role_id=1)
-- 已初始化数据库可重复执行，不影响其它数据。
-- PostgreSQL 语法（ON CONFLICT DO NOTHING）。
-- 菜单/按钮 ID 与 cache/code_permission 段位推后：menu=7, buttons=601~604。

-- 1) 数据字典菜单（parent_id=1 系统管理）
INSERT INTO sys_permission
    (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status)
VALUES
    (7, '数据字典', 'system:dict:list', 1, 1, '/system/dict/list', 'system/dict/list', 'Notebook', 6, 1)
ON CONFLICT (id) DO NOTHING;

-- 2) 数据字典按钮（parent_id=7）
INSERT INTO sys_permission
    (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status)
VALUES
    (601, '字典新增', 'system:dict:add',    2, 7, NULL, NULL, NULL, 1, 1),
    (602, '字典编辑', 'system:dict:edit',   2, 7, NULL, NULL, NULL, 2, 1),
    (603, '字典查看', 'system:dict:view',   2, 7, NULL, NULL, NULL, 3, 1),
    (604, '字典删除', 'system:dict:delete', 2, 7, NULL, NULL, NULL, 4, 1)
ON CONFLICT (id) DO NOTHING;

-- 3) 绑定到 super_admin（role_id=1）
-- id 与 permission_id 对齐（同 init_rbac）；UK (role_id, permission_id)
INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT p.id, 1, p.id
FROM sys_permission p
WHERE p.id IN (7, 601, 602, 603, 604)
ON CONFLICT (role_id, permission_id) DO NOTHING;
