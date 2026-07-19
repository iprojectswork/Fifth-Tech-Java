package com.fifthtech.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.dao.entity.user.User;

import java.util.List;

/**
 * UserService
 *
 * @author RH
 * @description 用户服务接口
 * @date 2026-01-25
 * @version 1.0
 */
public interface UserService extends IService<User> {

    User insert(User user);

    boolean delete(Long id);

    boolean edit(User user);

    User selectById(Long id);

    Page<User> list(Integer current, Integer size, User query);

    User selectByUsername(String username);

    boolean updateStatus(List<Long> ids, Integer status);
}
