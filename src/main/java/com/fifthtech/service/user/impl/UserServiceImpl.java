package com.fifthtech.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dao.mapper.permission.PermissionMapper;
import com.fifthtech.dao.mapper.role.RoleMapper;
import com.fifthtech.dao.mapper.user.UserMapper;
import com.fifthtech.service.user.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserServiceImpl
 *
 * @author RH
 * @description 用户服务实现类
 * @date 2026-01-25
 * @version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User insert(User user) {
        save(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        User user = getById(id);
        if (user == null) {
            return false;
        }
        return removeById(id);
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
    public Page<User> list(Integer current, Integer size, User query) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            wrapper.like(query.getUsername() != null, User::getUsername, query.getUsername())
                    .like(query.getNickname() != null, User::getNickname, query.getNickname())
                    .like(query.getEmail() != null, User::getEmail, query.getEmail())
                    .like(query.getPhone() != null, User::getPhone, query.getPhone())
                    .eq(query.getStatus() != null, User::getStatus, query.getStatus());
        }
        return page(page, wrapper);
    }

    @Override
    public User selectByUsername(String username) {
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
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(User::getId, ids)
                .set(User::getStatus, status)
                .set(User::getUpdateTime, LocalDateTime.now());
        return update(wrapper);
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
}
