package com.fifthtech.dao.mapper.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.role.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author RH
 * @ClassName RolePermissionMapper
 * @description: 角色权限关联Mapper接口
 * @date 2026年03月22日
 * @version: 1.0
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
    * @description: 根据角色id删除全部权限绑定
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [roleId]
    * @return: void
    **/
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
    * @description: 批量插入角色权限绑定（一次性 VALUES 多行）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [roleId, permissionIds]
    * @return: void
    **/
    void batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
}
