package com.fifthtech.service.role.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dao.mapper.role.RoleMapper;
import com.fifthtech.dao.mapper.role.RolePermissionMapper;
import com.fifthtech.dto.role.RoleDTO;
import com.fifthtech.security.UserContext;
import com.fifthtech.service.role.RoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RoleServiceImpl
 *
 * <p>角色是全局权限包，不挂组织；{@code roleCode} 全局唯一。</p>
 *
 * @author RH
 * @description 角色服务实现
 * @date 2026-03-22
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public Page<Role> selectPage(Integer current, Integer size, String roleName, String roleCode) {
        Page<Role> page = new Page<>(current, size);
        IPage<Role> result = baseMapper.listPage(page, roleName, roleCode);
        return (Page<Role>) result;
    }

    @Override
    public Role selectById(Long id) {
        if (id == null) {
            return null;
        }
        return getById(id);
    }

    @Override
    public List<Role> selectAll() {
        List<Role> roles = baseMapper.selectAllEnabled();
        return roles == null ? new ArrayList<>() : roles;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role insert(RoleDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        if (dto.getRoleCode() == null || dto.getRoleCode().trim().isEmpty()) {
            throw new IllegalArgumentException("roleCode 不能为空");
        }
        String code = dto.getRoleCode().trim();
        long dup = baseMapper.countByRoleCode(code, null);
        if (dup > 0) {
            throw new IllegalArgumentException("roleCode 已存在");
        }
        Role role = ConvertUtils.toEntity(dto, Role.class);
        role.setRoleCode(code);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        role.setCreateTime(LocalDateTime.now());
        Long uid = UserContext.getCurrentUserId();
        if (uid != null) {
            role.setCreateId(uid);
        }
        save(role);
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role update(RoleDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        Role existRole = getById(dto.getId());
        if (existRole == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        if (dto.getRoleCode() != null && !dto.getRoleCode().trim().isEmpty()) {
            String newCode = dto.getRoleCode().trim();
            if (!newCode.equals(existRole.getRoleCode())) {
                long dup = baseMapper.countByRoleCode(newCode, existRole.getId());
                if (dup > 0) {
                    throw new IllegalArgumentException("roleCode 已存在");
                }
                existRole.setRoleCode(newCode);
            }
        }
        if (dto.getRoleName() != null) {
            existRole.setRoleName(dto.getRoleName());
        }
        if (dto.getDescription() != null) {
            existRole.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            existRole.setStatus(dto.getStatus());
        }
        if (dto.getSort() != null) {
            existRole.setSort(dto.getSort());
        }
        existRole.setUpdateTime(LocalDateTime.now());
        Long uid = UserContext.getCurrentUserId();
        if (uid != null) {
            existRole.setUpdateId(uid);
        }
        updateById(existRole);
        return getById(existRole.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (id == null) {
            return;
        }
        Role role = getById(id);
        if (role == null) {
            return;
        }
        Long uid = UserContext.getCurrentUserId();
        if (uid != null) {
            role.setDeleteId(uid);
            role.setDeleteTime(LocalDateTime.now());
        }
        removeById(id);
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return baseMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Long> dedup = new HashSet<>(permissionIds);
            rolePermissionMapper.batchInsert(roleId, new ArrayList<>(dedup));
        }
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return baseMapper.selectByUserId(userId);
    }
}
