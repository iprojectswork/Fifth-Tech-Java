package com.fifthtech.service.role;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dto.role.RoleDTO;
import com.fifthtech.dto.role.RoleQueryDTO;

import java.util.List;

/**
 * @author RH
 * @ClassName RoleService
 * @description: 角色服务接口
 * @date 2026年03月22日
 * @version: 1.0
 */
public interface RoleService extends IService<Role> {

    /**
    * @description: 分页查询角色，按 roleName/roleCode 模糊过滤
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Page}<{@link Role}>
    **/
    Page<Role> selectPage(RoleQueryDTO query);

    /**
    * @description: 根据id查询角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Role}
    **/
    Role selectById(Long id);

    /**
    * @description: 查询全部启用角色（按 sort/id 升序），供下拉选择
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link Role}>
    **/
    List<Role> selectAll();

    /**
    * @description: 新增角色，roleCode 全局唯一
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Role}
    **/
    Role insert(RoleDTO dto);

    /**
    * @description: 根据id修改角色，roleCode 变更时排除自身校验全局唯一
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Role}
    **/
    Role update(RoleDTO dto);

    /**
    * @description: 根据id逻辑删除角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: void
    **/
    void deleteById(Long id);

    /**
    * @description: 根据角色id查询已绑定权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [roleId]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> getPermissionIdsByRoleId(Long roleId);

    /**
    * @description: 根据角色id全量替换权限绑定
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [roleId, permissionIds]
    * @return: void
    **/
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
    * @description: 根据用户id查询已绑定启用角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link List}<{@link Role}>
    **/
    List<Role> getRolesByUserId(Long userId);
}
