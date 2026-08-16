package com.fifthtech.service.permission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.common.BizConstants;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dto.permission.PermissionDTO;
import com.fifthtech.dto.permission.PermissionQueryDTO;
import com.fifthtech.vo.permission.PermissionTreeVO;
import com.fifthtech.vo.permission.PermissionVO;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dao.mapper.permission.PermissionMapper;
import com.fifthtech.security.UserContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author RH
 * @ClassName PermissionServiceImpl
 * @description: 权限服务实现
 * @date 2026年03月22日
 * @version: 1.0
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements com.fifthtech.service.permission.PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public Page<Permission> selectPage(PermissionQueryDTO query) {
        if (query == null) {
            query = new PermissionQueryDTO();
        }
        int current = query.getCurrent() == null ? BizConstants.DEFAULT_PAGE_CURRENT : query.getCurrent();
        int size = query.getSize() == null ? BizConstants.DEFAULT_PAGE_SIZE : query.getSize();
        Page<Permission> page = new Page<>(current, size);
        String permissionName = query.getPermissionName();
        String permissionCode = query.getPermissionCode();
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
        return buildTree(voList, BizConstants.ROOT_PARENT_ID);
    }

    @Override
    public List<PermissionVO> listChildren(PermissionQueryDTO query) {
        long parentId = (query == null || query.getParentId() == null)
                ? BizConstants.ROOT_PARENT_ID : query.getParentId();
        List<Permission> list = permissionMapper.selectByParentId(parentId);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<PermissionVO> vos = ConvertUtils.toVOList(list, PermissionVO.class);
        fillHasChildren(vos);
        return vos;
    }

    private List<PermissionTreeVO> buildTree(List<PermissionTreeVO> permissions, Long parentId) {
        List<PermissionTreeVO> tree = new ArrayList<>();
        for (PermissionTreeVO permission : permissions) {
            Long actualParentId = permission.getParentId() == null
                    ? BizConstants.ROOT_PARENT_ID : permission.getParentId();
            if (parentId.equals(actualParentId)) {
                List<PermissionTreeVO> children = buildTree(permissions, permission.getId());
                permission.setChildren(children);
                permission.setHasChildren(children != null && !children.isEmpty());
                tree.add(permission);
            }
        }
        return tree;
    }

    private void fillHasChildren(List<PermissionVO> vos) {
        if (vos == null || vos.isEmpty()) {
            return;
        }
        List<Long> ids = new ArrayList<>();
        for (PermissionVO vo : vos) {
            if (vo.getId() != null) {
                ids.add(vo.getId());
            }
        }
        if (ids.isEmpty()) {
            return;
        }
        List<Long> parentIds = permissionMapper.selectParentIdsHavingChildren(ids);
        Set<Long> withChildren = parentIds == null ? Collections.emptySet() : new HashSet<>(parentIds);
        for (PermissionVO vo : vos) {
            vo.setHasChildren(vo.getId() != null && withChildren.contains(vo.getId()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Permission insert(PermissionDTO dto) {
        Permission permission = ConvertUtils.toEntity(dto, Permission.class);
        permission.setStatus(dto.getStatus() != null ? dto.getStatus() : BizConstants.STATUS_ENABLED);
        permission.setParentId(dto.getParentId() != null ? dto.getParentId() : BizConstants.ROOT_PARENT_ID);
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
        if (menuPermissions == null || menuPermissions.isEmpty()) {
            return Collections.emptyList();
        }
        List<Permission> withAncestors = expandMenuAncestors(menuPermissions);
        List<PermissionTreeVO> voList = ConvertUtils.toVOList(withAncestors, PermissionTreeVO.class);
        return buildTree(voList, BizConstants.ROOT_PARENT_ID);
    }

    private List<Permission> expandMenuAncestors(List<Permission> grantedMenus) {
        List<Permission> allMenus = lambdaQuery()
                .eq(Permission::getPermissionType, BizConstants.PERMISSION_TYPE_MENU)
                .eq(Permission::getStatus, BizConstants.STATUS_ENABLED)
                .list();
        Map<Long, Permission> byId = new LinkedHashMap<>();
        for (Permission permission : allMenus) {
            if (permission.getId() != null) {
                byId.put(permission.getId(), permission);
            }
        }

        Map<Long, Permission> expanded = new LinkedHashMap<>();
        for (Permission leaf : grantedMenus) {
            if (leaf.getId() == null) {
                continue;
            }
            expanded.put(leaf.getId(), leaf);
            Long parentId = leaf.getParentId();
            while (parentId != null && parentId != BizConstants.ROOT_PARENT_ID) {
                if (expanded.containsKey(parentId)) {
                    break;
                }
                Permission parent = byId.get(parentId);
                if (parent == null) {
                    break;
                }
                expanded.put(parent.getId(), parent);
                parentId = parent.getParentId();
            }
        }
        return new ArrayList<>(expanded.values());
    }
}