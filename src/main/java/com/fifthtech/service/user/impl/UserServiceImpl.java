package com.fifthtech.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.common.BizConstants;
import com.fifthtech.dao.entity.org.Org;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dao.entity.user.UserRole;
import com.fifthtech.dao.mapper.org.OrgMapper;
import com.fifthtech.dao.mapper.org.UserOrgMapper;
import com.fifthtech.dao.mapper.permission.PermissionMapper;
import com.fifthtech.dao.mapper.role.RoleMapper;
import com.fifthtech.dao.mapper.user.UserMapper;
import com.fifthtech.dao.mapper.user.UserRoleMapper;
import com.fifthtech.dto.user.UserDTO;
import com.fifthtech.dto.user.UserQueryDTO;
import com.fifthtech.service.org.OrgService;
import com.fifthtech.service.user.UserService;
import com.fifthtech.vo.user.UserOrgSummaryVO;
import com.fifthtech.vo.user.UserRoleSummaryVO;
import com.fifthtech.vo.user.UserVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author RH
 * @ClassName UserServiceImpl
 * @description: 用户服务实现
 * @date 2026年01月25日
 * @version: 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private OrgMapper orgMapper;

    @Resource
    private UserOrgMapper userOrgMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private OrgService orgService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User insert(User user) {
        save(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return deleteUser(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(User user) {
        User existUser = getById(user.getId());
        if (existUser == null) {
            return false;
        }
        return updateById(user);
    }

    @Override
    public User selectById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User insertWithMemberships(UserDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("username 不能为空");
        }
        // 唯一性：用户名
        long sameName = baseMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername().trim()));
        if (sameName > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername().trim());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        } else {
            user.setPassword(""); // 不强制；后续可改成必填
        }
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(dto.getStatus() == null ? BizConstants.STATUS_ENABLED : dto.getStatus());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        save(user);
        Long userId = user.getId();
        List<Long> roleIds = dto.getRoleIds() == null ? null : normalizeLongList(dto.getRoleIds());
        if (roleIds != null && !roleIds.isEmpty()) {
            Map<Long, Role> roles = validateAndLoadRoles(roleIds);
            ensureUserRoles(userId, new ArrayList<>(roles.keySet()));
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User editWithMemberships(UserDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        User existUser = getById(dto.getId());
        if (existUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (dto.getNickname() != null) {
            existUser.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            existUser.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            existUser.setPhone(dto.getPhone());
        }
        if (dto.getStatus() != null) {
            existUser.setStatus(dto.getStatus());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existUser.setPassword(dto.getPassword());
        }
        existUser.setUpdateTime(LocalDateTime.now());
        updateById(existUser);
        Long userId = existUser.getId();
        List<Long> newRoleIds = dto.getRoleIds() == null ? null : normalizeLongList(dto.getRoleIds());
        if (newRoleIds != null) {
            Map<Long, Role> newRoles = newRoleIds.isEmpty()
                    ? new HashMap<>()
                    : validateAndLoadRoles(newRoleIds);
            userRoleMapper.deleteByUserId(userId);
            if (!newRoles.isEmpty()) {
                ensureUserRoles(userId, new ArrayList<>(newRoles.keySet()));
            }
        }
        return existUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        if (id == null) {
            return false;
        }
        User exist = getById(id);
        if (exist == null) {
            return false;
        }
        // 级联清 sys_user_role
        userRoleMapper.deleteByUserId(id);
        // 级联清 sys_user_org
        userOrgMapper.deleteByUserId(id);
        return removeById(id);
    }

    @Override
    public UserVO infoWithMemberships(Long id) {
        if (id == null) {
            return null;
        }
        User user = getById(id);
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        // orgs
        List<Long> orgIds = userOrgMapper.selectOrgIdsByUserId(id);
        if (orgIds == null || orgIds.isEmpty()) {
            vo.setOrgs(new ArrayList<>());
            vo.setOrgNames("");
        } else {
            LambdaQueryWrapper<Org> orgWrapper = new LambdaQueryWrapper<>();
            orgWrapper.in(Org::getId, orgIds).eq(Org::getDeleted, BizConstants.NOT_DELETED)
                    .orderByAsc(Org::getSort, Org::getOrgCode);
            List<Org> orgs = orgMapper.selectList(orgWrapper);
            Map<Long, Org> orgMap = new HashMap<>();
            for (Org org : orgs) {
                orgMap.put(org.getId(), org);
            }
            Map<Long, String> pathMap = orgService.pathNames(orgIds);
            List<UserOrgSummaryVO> summaries = new ArrayList<>();
            StringBuilder nameSb = new StringBuilder();
            for (Long orgId : orgIds) {
                Org org = orgMap.get(orgId);
                if (org == null) {
                    continue;
                }
                String pathName = pathMap.get(orgId);
                if (pathName == null || pathName.isEmpty()) {
                    pathName = org.getOrgName();
                }
                UserOrgSummaryVO summary = new UserOrgSummaryVO();
                summary.setId(org.getId());
                summary.setOrgCode(org.getOrgCode());
                summary.setOrgName(org.getOrgName());
                summary.setPathName(pathName);
                summaries.add(summary);
                if (nameSb.length() > 0) {
                    nameSb.append("、");
                }
                nameSb.append(pathName);
            }
            vo.setOrgs(summaries);
            vo.setOrgNames(nameSb.toString());
        }
        // roles
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(id);
        if (roleIds == null || roleIds.isEmpty()) {
            vo.setRoles(new ArrayList<>());
        } else {
            LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.in(Role::getId, roleIds).eq(Role::getDeleted, BizConstants.NOT_DELETED)
                    .orderByAsc(Role::getSort, Role::getId);
            List<Role> roles = roleMapper.selectList(roleWrapper);
            List<UserRoleSummaryVO> roleSummaries = new ArrayList<>();
            for (Role role : roles) {
                UserRoleSummaryVO summary = new UserRoleSummaryVO();
                summary.setId(role.getId());
                summary.setRoleCode(role.getRoleCode());
                summary.setRoleName(role.getRoleName());
                roleSummaries.add(summary);
            }
            vo.setRoles(roleSummaries);
        }
        return vo;
    }

    @Override
    public Page<User> list(UserQueryDTO query) {
        if (query == null) {
            query = new UserQueryDTO();
        }
        int current = query.getCurrent() == null || query.getCurrent() < 1
                ? BizConstants.DEFAULT_PAGE_CURRENT : query.getCurrent();
        int size = query.getSize() == null || query.getSize() < 1
                ? BizConstants.DEFAULT_PAGE_SIZE : query.getSize();
        Page<User> page = new Page<>(current, size);
        query.setUsername(blankToNull(query.getUsername()));
        query.setNickname(blankToNull(query.getNickname()));
        query.setEmail(blankToNull(query.getEmail()));
        query.setPhone(blankToNull(query.getPhone()));
        if (query.getOrgId() != null) {
            query.setOrgIds(orgService.subtreeOrgIds(query.getOrgId()));
        }
        IPage<User> result = baseMapper.listPage(page, query);
        return (Page<User>) result;
    }

    @Override
    public User selectByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty() || status == null) {
            return false;
        }
        return lambdaUpdate()
                .in(User::getId, ids)
                .set(User::getStatus, status)
                .set(User::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    public Page<Role> listRoles(UserQueryDTO query) {
        if (query == null) {
            query = new UserQueryDTO();
        }
        int current = query.getCurrent() == null || query.getCurrent() < 1
                ? BizConstants.DEFAULT_PAGE_CURRENT : query.getCurrent();
        int size = query.getSize() == null || query.getSize() < 1
                ? BizConstants.DEFAULT_PAGE_SIZE : query.getSize();
        Page<Role> page = new Page<>(current, size);
        if (query.getUserId() == null) {
            return page;
        }
        return (Page<Role>) roleMapper.listRelatedByUserId(page, query.getUserId());
    }

    @Override
    public Page<Permission> listPermissions(UserQueryDTO query) {
        if (query == null) {
            query = new UserQueryDTO();
        }
        int current = query.getCurrent() == null || query.getCurrent() < 1
                ? BizConstants.DEFAULT_PAGE_CURRENT : query.getCurrent();
        int size = query.getSize() == null || query.getSize() < 1
                ? BizConstants.DEFAULT_PAGE_SIZE : query.getSize();
        Page<Permission> page = new Page<>(current, size);
        if (query.getUserId() == null) {
            return page;
        }
        return (Page<Permission>) permissionMapper.listRelatedByUserId(page, query.getUserId());
    }

    private Map<Long, Role> validateAndLoadRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(Role::getId, roleIds).eq(Role::getDeleted, BizConstants.NOT_DELETED);
        List<Role> roles = roleMapper.selectList(roleWrapper);
        Map<Long, Role> map = new HashMap<>();
        for (Role role : roles) {
            map.put(role.getId(), role);
        }
        if (map.size() != roleIds.size()) {
            throw new IllegalArgumentException("部分角色不存在或已删除");
        }
        return map;
    }

    private void ensureUserRoles(Long userId, List<Long> roleIds) {
        if (userId == null || roleIds == null || roleIds.isEmpty()) {
            return;
        }
        Set<Long> existing = new HashSet<>(userRoleMapper.selectRoleIdsByUserId(userId));
        for (Long roleId : roleIds) {
            if (roleId == null || existing.contains(roleId)) {
                continue;
            }
            UserRole row = new UserRole();
            row.setUserId(userId);
            row.setRoleId(roleId);
            row.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(row);
            existing.add(roleId);
        }
    }

    private List<Long> normalizeLongList(List<Long> raw) {
        if (raw == null) {
            return new ArrayList<>();
        }
        Set<Long> dedup = new HashSet<>();
        for (Long id : raw) {
            if (id != null) {
                dedup.add(id);
            }
        }
        return new ArrayList<>(dedup);
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @SuppressWarnings("unused")
    private static <T> List<T> safeList(List<T> src) {
        return src == null ? Collections.emptyList() : src;
    }
}