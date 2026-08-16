package com.fifthtech.service.permission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.vo.permission.PermissionTreeVO;
import com.fifthtech.vo.permission.PermissionVO;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dto.permission.PermissionDTO;
import com.fifthtech.dto.permission.PermissionQueryDTO;

import java.util.List;

/**
 * @author RH
 * @ClassName PermissionService
 * @description: 权限服务接口
 * @date 2026年03月22日
 * @version: 1.0
 */
public interface PermissionService extends IService<Permission> {

    /**
    * @description: 分页查询权限，按 permissionName/permissionCode 模糊过滤
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Page}<{@link Permission}>
    **/
    Page<Permission> selectPage(PermissionQueryDTO query);

    /**
    * @description: 根据id查询权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Permission}
    **/
    Permission selectById(Long id);

    /**
    * @description: 查询全部未删权限（按 sort 升序），用于分配角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link Permission}>
    **/
    List<Permission> selectAll();

    /**
    * @description: 查询全部未删权限并组装成树（按 sort 升序）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link PermissionTreeVO}>
    **/
    List<PermissionTreeVO> selectTree();

    /**
    * @description: 根据父id查询直接子权限，含是否有子节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link List}<{@link PermissionVO}>
    **/
    List<PermissionVO> listChildren(PermissionQueryDTO query);

    /**
    * @description: 新增权限，parentId 缺省取根
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Permission}
    **/
    Permission insert(PermissionDTO dto);

    /**
    * @description: 根据id修改权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Permission}
    **/
    Permission update(PermissionDTO dto);

    /**
    * @description: 根据id逻辑删除权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: void
    **/
    void deleteById(Long id);

    /**
    * @description: 根据用户id查询已启用权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link List}<{@link Permission}>
    **/
    List<Permission> getPermissionsByUserId(Long userId);

    /**
    * @description: 根据用户id查询已启用菜单权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link List}<{@link Permission}>
    **/
    List<Permission> getMenuPermissionsByUserId(Long userId);

    /**
    * @description: 根据用户id查询已启用权限编码
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link List}<{@link String}>
    **/
    List<String> getPermissionCodesByUserId(Long userId);

    /**
    * @description: 根据用户id查询菜单树，含祖先节点补齐
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link List}<{@link PermissionTreeVO}>
    **/
    List<PermissionTreeVO> getMenuTreeByUserId(Long userId);
}
