package com.fifthtech.service.role;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dto.role.RoleDTO;

import java.util.List;

public interface RoleService extends IService<Role> {

    Page<Role> selectPage(Integer current, Integer size, String roleName, String roleCode);

    Role selectById(Long id);

    List<Role> selectAll();

    Role insert(RoleDTO dto);

    Role update(RoleDTO dto);

    void deleteById(Long id);

    List<Long> getPermissionIdsByRoleId(Long roleId);

    void assignPermissions(Long roleId, List<Long> permissionIds);

    List<Role> getRolesByUserId(Long userId);
}
