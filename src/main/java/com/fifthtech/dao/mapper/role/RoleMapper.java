package com.fifthtech.dao.mapper.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dto.role.RoleQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author RH
 * @ClassName RoleMapper
 * @description: 角色Mapper接口
 * @date 2026年03月22日
 * @version: 1.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
    * @description: 根据用户id查询启用角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link List}<{@link Role}>
    **/
    List<Role> selectByUserId(@Param("userId") Long userId);

    /**
    * @description: 根据用户id分页查询关联角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [page, userId]
    * @return: {@link IPage}<{@link Role}>
    **/
    IPage<Role> listRelatedByUserId(Page<Role> page, @Param("userId") Long userId);

    /**
    * @description: 分页查询角色，按 roleName/roleCode 模糊过滤（按 sort/id 升序）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [page, query]
    * @return: {@link IPage}<{@link Role}>
    **/
    IPage<Role> listPage(Page<Role> page, @Param("query") RoleQueryDTO query);

    /**
    * @description: 查询全部启用角色（按 sort/id 升序）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link Role}>
    **/
    List<Role> selectAllEnabled();

    /**
    * @description: 根据角色id查询已绑定权限id
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [roleId]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
    * @description: 根据角色编码统计未删角色数
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [roleCode, excludeId]
    * @return: long
    **/
    long countByRoleCode(@Param("roleCode") String roleCode,
                         @Param("excludeId") Long excludeId);
}
