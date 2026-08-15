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
 * RoleMapper
 *
 * <p>角色 Mapper。{@code list} / {@code all} / 用户角色联查走 XML 手写 SQL（禁 {@code SELECT *}）。</p>
 *
 * @author RH
 * @description 角色 Mapper
 * @date 2026-03-22
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 鉴权用：按用户 ID 拉启用的角色列表。
     */
    List<Role> selectByUserId(@Param("userId") Long userId);

    /**
     * 用户详情：关联角色子表（含禁用）。
     */
    IPage<Role> listRelatedByUserId(Page<Role> page, @Param("userId") Long userId);

    /**
     * /role/list：可选 name/code 模糊。
     */
    IPage<Role> listPage(Page<Role> page, @Param("query") RoleQueryDTO query);

    /**
     * /role/all：返回启用角色。
     */
    List<Role> selectAllEnabled();

    /**
     * 根据角色ID查询权限ID列表
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 未删 roleCode 全局唯一性校验（{@code excludeId} 可选：edit 时排除自身）。
     */
    long countByRoleCode(@Param("roleCode") String roleCode,
                         @Param("excludeId") Long excludeId);
}
