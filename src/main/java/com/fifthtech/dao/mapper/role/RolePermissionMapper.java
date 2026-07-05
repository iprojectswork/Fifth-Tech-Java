package com.fifthtech.dao.mapper.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.role.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * RolePermissionMapper
 *
 * @author RH
 * @description 角色权限关联Mapper接口
 * @date 2026-03-22
 * @version 1.0
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 根据角色ID删除角色权限关联
     */
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限关联
     */
    void batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
}
