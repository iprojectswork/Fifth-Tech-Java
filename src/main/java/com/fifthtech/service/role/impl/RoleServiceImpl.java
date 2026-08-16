package com.fifthtech.service.role.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.common.BizConstants;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dao.mapper.role.RoleMapper;
import com.fifthtech.dao.mapper.role.RolePermissionMapper;
import com.fifthtech.dto.role.RoleDTO;
import com.fifthtech.dto.role.RoleQueryDTO;
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
 * @author RH
 * @ClassName RoleServiceImpl
 * @description: 角色服务实现
 * @date 2026年03月22日
 * @version: 1.0
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public Page<Role> selectPage(RoleQueryDTO query) {
        if (query == null) {
            query = new RoleQueryDTO();
        }
        int current = query.getCurrent() == null ? BizConstants.DEFAULT_PAGE_CURRENT : query.getCurrent();
        int size = query.getSize() == null ? BizConstants.DEFAULT_PAGE_SIZE : query.getSize();
        Page<Role> page = new Page<>(current, size);
        IPage<Role> result = baseMapper.listPage(page, query);
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
        String roleCode = dto.getRoleCode().trim();
        long duplicateCount = baseMapper.countByRoleCode(roleCode, null);
        if (duplicateCount > 0) {
            throw new IllegalArgumentException("roleCode 已存在");
        }
        Role role = ConvertUtils.toEntity(dto, Role.class);
        role.setRoleCode(roleCode);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : BizConstants.STATUS_ENABLED);
        role.setCreateTime(LocalDateTime.now());
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            role.setCreateId(userId);
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
                long duplicateCount = baseMapper.countByRoleCode(newCode, existRole.getId());
                if (duplicateCount > 0) {
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
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            existRole.setUpdateId(userId);
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
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            role.setDeleteId(userId);
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