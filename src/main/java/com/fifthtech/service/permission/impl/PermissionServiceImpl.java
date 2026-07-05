package com.fifthtech.service.permission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.controller.permission.PermissionDTO;
import com.fifthtech.controller.permission.PermissionTreeVO;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dao.mapper.permission.PermissionMapper;
import com.fifthtech.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PermissionServiceImpl
 *
 * @author RH
 * @description 权限服务实现类
 * @date 2026-03-22
 * @version 1.0
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements com.fifthtech.service.permission.PermissionService {

    @Override
    public Page<Permission> selectPage(Integer current, Integer size, String permissionName, String permissionCode) {
        Page<Permission> page = new Page<>(current, size);
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(permissionName != null && !permissionName.isEmpty(), Permission::getPermissionName, permissionName)
                .like(permissionCode != null && !permissionCode.isEmpty(), Permission::getPermissionCode, permissionCode)
                .orderByAsc(Permission::getSort);
        return page(page, wrapper);
    }

    @Override
    public Permission selectById(Long id) {
        return getById(id);
    }

    @Override
    public List<Permission> selectAll() {
        return baseMapper.selectAllPermissions();
    }

    @Override
    public List<PermissionTreeVO> selectTree() {
        // 查询所有权限
        List<Permission> allPermissions = selectAll();
        // 转换为VO列表
        List<PermissionTreeVO> voList = ConvertUtils.toVOList(allPermissions, PermissionTreeVO.class);
        // 构建树结构
        return buildTree(voList, 0L);
    }

    /**
     * 构建权限树
     *
     * @param permissions 权限列表
     * @param parentId    父级ID
     * @return 权限树
     */
    private List<PermissionTreeVO> buildTree(List<PermissionTreeVO> permissions, Long parentId) {
        List<PermissionTreeVO> tree = new ArrayList<>();
        for (PermissionTreeVO permission : permissions) {
            if (parentId.equals(permission.getParentId())) {
                List<PermissionTreeVO> children = buildTree(permissions, permission.getId());
                permission.setChildren(children);
                tree.add(permission);
            }
        }
        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Permission insert(PermissionDTO dto) {
        Permission permission = ConvertUtils.toEntity(dto, Permission.class);
        permission.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        permission.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        permission.setCreateTime(LocalDateTime.now());
        // 设置创建人信息
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId != null) {
            permission.setCreateId(currentUserId);
        }
        save(permission);
        return permission;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Permission update(PermissionDTO dto) {
        Permission existPermission = getById(dto.getId());
        if (existPermission == null) {
            return null;
        }
        Permission permission = ConvertUtils.toEntity(dto, Permission.class);
        permission.setUpdateTime(LocalDateTime.now());
        // 设置更新人信息
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId != null) {
            permission.setUpdateId(currentUserId);
        }
        updateById(permission);
        return getById(dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        Permission permission = getById(id);
        if (permission != null) {
            // 设置删除信息
            Long currentUserId = UserContext.getCurrentUserId();
            if (currentUserId != null) {
                permission.setDeleteId(currentUserId);
                permission.setDeleteTime(LocalDateTime.now());
            }
            removeById(id);
        }
    }

    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<Permission> getMenuPermissionsByUserId(Long userId) {
        return baseMapper.selectMenuByUserId(userId);
    }

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        List<Permission> permissions = getPermissionsByUserId(userId);
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionTreeVO> getMenuTreeByUserId(Long userId) {
        List<Permission> menuPermissions = getMenuPermissionsByUserId(userId);
        List<PermissionTreeVO> voList = ConvertUtils.toVOList(menuPermissions, PermissionTreeVO.class);
        return buildTree(voList, 0L);
    }
}
