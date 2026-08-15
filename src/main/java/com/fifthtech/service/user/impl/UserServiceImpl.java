package com.fifthtech.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * UserServiceImpl
 *
 * <p>用户服务实现。用户表单只写角色；组织成员由组织管理页维护。</p>
 *
 * @author RH
 * @description 用户服务实现类
 * @date 2026-01-25
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

    // ---------------------------------------------------------------------
    // 旧版接口（保持兼容）
    // ---------------------------------------------------------------------

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

    // ---------------------------------------------------------------------
    // 新版：含成员与任职（写入路径）
    // ---------------------------------------------------------------------

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
        user.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
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
            LambdaQueryWrapper<Org> ow = new LambdaQueryWrapper<>();
            ow.in(Org::getId, orgIds).eq(Org::getDeleted, 0).orderByAsc(Org::getSort, Org::getOrgCode);
            List<Org> orgs = orgMapper.selectList(ow);
            Map<Long, Org> orgMap = new HashMap<>();
            for (Org o : orgs) {
                orgMap.put(o.getId(), o);
            }
            Map<Long, String> pathMap = orgService.pathNames(orgIds);
            List<UserOrgSummaryVO> summaries = new ArrayList<>();
            StringBuilder nameSb = new StringBuilder();
            for (Long oid : orgIds) {
                Org o = orgMap.get(oid);
                if (o == null) {
                    continue;
                }
                String pathName = pathMap.get(oid);
                if (pathName == null || pathName.isEmpty()) {
                    pathName = o.getOrgName();
                }
                UserOrgSummaryVO s = new UserOrgSummaryVO();
                s.setId(o.getId());
                s.setOrgCode(o.getOrgCode());
                s.setOrgName(o.getOrgName());
                s.setPathName(pathName);
                summaries.add(s);
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
            LambdaQueryWrapper<Role> rw = new LambdaQueryWrapper<>();
            rw.in(Role::getId, roleIds).eq(Role::getDeleted, 0).orderByAsc(Role::getSort, Role::getId);
            List<Role> roles = roleMapper.selectList(rw);
            List<UserRoleSummaryVO> rsummaries = new ArrayList<>();
            for (Role r : roles) {
                UserRoleSummaryVO s = new UserRoleSummaryVO();
                s.setId(r.getId());
                s.setRoleCode(r.getRoleCode());
                s.setRoleName(r.getRoleName());
                rsummaries.add(s);
            }
            vo.setRoles(rsummaries);
        }
        return vo;
    }

    // ---------------------------------------------------------------------
    // 旧版 list（兼容性保持，但改为接收 UserDTO）
    // ---------------------------------------------------------------------

    @Override
    public Page<User> list(Integer current, Integer size, UserDTO dto) {
        int c = current == null || current < 1 ? 1 : current;
        int s = size == null || size < 1 ? 10 : size;
        Page<User> page = new Page<>(c, s);
        String username = dto == null ? null : dto.getUsername();
        String nickname = dto == null ? null : dto.getNickname();
        String email = dto == null ? null : dto.getEmail();
        String phone = dto == null ? null : dto.getPhone();
        Integer status = dto == null ? null : dto.getStatus();
        Long orgId = dto == null ? null : dto.getOrgId();
        List<Long> orgIds = orgId == null ? null : orgService.subtreeOrgIds(orgId);
        IPage<User> result = baseMapper.listPage(page,
                blankToNull(username), blankToNull(nickname), blankToNull(email), blankToNull(phone),
                status, orgIds);
        return (Page<User>) result;
    }

    // 兼容旧 User 查询入口：保留以防其它类误用
    public Page<User> list(Integer current, Integer size, User query) {
        UserDTO dto = new UserDTO();
        if (query != null) {
            dto.setUsername(query.getUsername());
            dto.setNickname(query.getNickname());
            dto.setEmail(query.getEmail());
            dto.setPhone(query.getPhone());
            dto.setStatus(query.getStatus());
        }
        return list(current, size, dto);
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
    public Page<Role> listRoles(Long userId, Integer current, Integer size) {
        int c = current == null || current < 1 ? 1 : current;
        int s = size == null || size < 1 ? 10 : size;
        Page<Role> page = new Page<>(c, s);
        if (userId == null) {
            return page;
        }
        return (Page<Role>) roleMapper.listRelatedByUserId(page, userId);
    }

    @Override
    public Page<Permission> listPermissions(Long userId, Integer current, Integer size) {
        int c = current == null || current < 1 ? 1 : current;
        int s = size == null || size < 1 ? 10 : size;
        Page<Permission> page = new Page<>(c, s);
        if (userId == null) {
            return page;
        }
        return (Page<Permission>) permissionMapper.listRelatedByUserId(page, userId);
    }

    // ---------------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------------

    /**
     * 校验角色均存在未删，返回 roleId → Role 的映射。
     */
    private Map<Long, Role> validateAndLoadRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<Role> w = new LambdaQueryWrapper<>();
        w.in(Role::getId, roleIds).eq(Role::getDeleted, 0);
        List<Role> roles = roleMapper.selectList(w);
        Map<Long, Role> map = new HashMap<>();
        for (Role r : roles) {
            map.put(r.getId(), r);
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
        for (Long v : raw) {
            if (v != null) {
                dedup.add(v);
            }
        }
        return new ArrayList<>(dedup);
    }

    private String blankToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // 工具：避免空集合时 MyBatis foreach 解析异常（业务层兜底）
    @SuppressWarnings("unused")
    private static <T> List<T> safeList(List<T> src) {
        return src == null ? Collections.emptyList() : src;
    }
}