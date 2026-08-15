-- C4 增量种子：组织管理菜单 + 按钮 + 绑定 super_admin(role_id=1)
-- PostgreSQL（ON CONFLICT DO NOTHING）。
-- 菜单 id=8，按钮 701~704。

INSERT INTO sys_permission
    (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status)
VALUES
    (8, '组织管理', 'system:org:list', 1, 1, '/system/org/list', 'system/org/list', 'OfficeBuilding', 7, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission
    (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort, status)
VALUES
    (701, '组织新增', 'system:org:add',    2, 8, NULL, NULL, NULL, 1, 1),
    (702, '组织编辑', 'system:org:edit',   2, 8, NULL, NULL, NULL, 2, 1),
    (703, '组织查看', 'system:org:view',   2, 8, NULL, NULL, NULL, 3, 1),
    (704, '组织删除', 'system:org:delete', 2, 8, NULL, NULL, NULL, 4, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT p.id, 1, p.id
FROM sys_permission p
WHERE p.id IN (8, 701, 702, 703, 704)
ON CONFLICT (role_id, permission_id) DO NOTHING;
