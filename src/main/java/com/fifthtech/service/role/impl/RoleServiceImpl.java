package com.fifthtech.service.role.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dto.role.RoleDTO;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dao.mapper.role.RoleMapper;
import com.fifthtech.dao.mapper.role.RolePermissionMapper;
import com.fifthtech.dao.mapper.user.UserRoleMapper;
import com.fifthtech.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RoleServiceImpl
 *
 * @author RH
 * @description 角色服务实现类
 * @date 2026-03-22
 * @version 1.0
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements com.fifthtech.service.role.RoleService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Page<Role> selectPage(Integer current, Integer size, String roleName, String roleCode) {
        Page<Role> page = new Page<>(current, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(roleName != null && !roleName.isEmpty(), Role::getRoleName, roleName)
                .like(roleCode != null && !roleCode.isEmpty(), Role::getRoleCode, roleCode)
                .orderByAsc(Role::getSort);
        return page(page, wrapper);
    }

    @Override
    public Role selectById(Long id) {
        return getById(id);
    }

    @Override
    public List<Role> selectAll() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1)
                .orderByAsc(Role::getSort);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role insert(RoleDTO dto) {
        Role role = ConvertUtils.toEntity(dto, Role.class);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        role.setCreateTime(LocalDateTime.now());
        // 设置创建人信息
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId != null) {
            role.setCreateId(currentUserId);
        }
        save(role);
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role update(RoleDTO dto) {
        Role existRole = getById(dto.getId());
        if (existRole == null) {
            return null;
        }
        Role role = ConvertUtils.toEntity(dto, Role.class);
        role.setUpdateTime(LocalDateTime.now());
        // 设置更新人信息
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId != null) {
            role.setUpdateId(currentUserId);
        }
        updateById(role);
        return getById(dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        Role role = getById(id);
        if (role != null) {
            // 设置删除信息
            Long currentUserId = UserContext.getCurrentUserId();
            if (currentUserId != null) {
                role.setDeleteId(currentUserId);
                role.setDeleteTime(LocalDateTime.now());
            }
            removeById(id);
        }
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return baseMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除原有权限
        rolePermissionMapper.deleteByRoleId(roleId);
        // 再批量插入新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.batchInsert(roleId, permissionIds);
        }
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }
}
