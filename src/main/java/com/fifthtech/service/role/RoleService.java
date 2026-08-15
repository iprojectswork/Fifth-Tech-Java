package com.fifthtech.service.role;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dto.role.RoleDTO;

import java.util.List;

/**
 * RoleService
 *
 * <p>角色服务。角色是全局权限包，不挂组织；{@code roleCode} 全局唯一。</p>
 *
 * @author RH
 * @description 角色服务接口
 * @date 2026-03-22
 */
public interface RoleService extends IService<Role> {

    /**
     * /role/list：分页；roleName / roleCode 可选模糊。
     */
    Page<Role> selectPage(Integer current, Integer size, String roleName, String roleCode);

    Role selectById(Long id);

    /**
     * /role/all：返回启用角色。
     */
    List<Role> selectAll();

    /**
     * 新增：roleCode 全局唯一。
     */
    Role insert(RoleDTO dto);

    /**
     * 修改：roleCode 全局唯一（排除自身）。
     */
    Role update(RoleDTO dto);

    void deleteById(Long id);

    List<Long> getPermissionIdsByRoleId(Long roleId);

    void assignPermissions(Long roleId, List<Long> permissionIds);

    List<Role> getRolesByUserId(Long userId);
}
