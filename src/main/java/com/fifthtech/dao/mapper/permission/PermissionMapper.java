package com.fifthtech.dao.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.dao.entity.permission.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author RH
 * @ClassName PermissionMapper
 * @description: 权限Mapper接口
 * @date 2026年03月22日
 * @version: 1.0
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
    * @description: 根据用户id查询已启用权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link List}<{@link Permission}>
    **/
    List<Permission> selectByUserId(@Param("userId") Long userId);

    /**
    * @description: 根据用户id分页查询关联权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [page, userId]
    * @return: {@link IPage}<{@link Permission}>
    **/
    IPage<Permission> listRelatedByUserId(Page<Permission> page, @Param("userId") Long userId);

    /**
    * @description: 根据用户id查询已启用菜单权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link List}<{@link Permission}>
    **/
    List<Permission> selectMenuByUserId(@Param("userId") Long userId);

    /**
    * @description: 查询全部未删权限（按 sort 升序），用于角色分配
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link Permission}>
    **/
    List<Permission> selectAllPermissions();

    /**
    * @description: 根据父id查询直接子权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [parentId]
    * @return: {@link List}<{@link Permission}>
    **/
    List<Permission> selectByParentId(@Param("parentId") Long parentId);

    /**
    * @description: 从父ID集合中筛选出「有未删子」的子集，用于树/children 的 hasChildren 标记
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [parentIds]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> selectParentIdsHavingChildren(@Param("parentIds") List<Long> parentIds);
}
