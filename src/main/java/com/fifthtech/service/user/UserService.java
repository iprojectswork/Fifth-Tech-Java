package com.fifthtech.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dto.user.UserDTO;
import com.fifthtech.dto.user.UserQueryDTO;
import com.fifthtech.vo.user.UserVO;

import java.util.List;

/**
 * @author RH
 * @ClassName UserService
 * @description: 用户服务接口
 * @date 2026年01月25日
 * @version: 1.0
 */
public interface UserService extends IService<User> {

    /**
    * @description: 新增用户并写入角色，不写组织成员
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link User}
    **/
    User insertWithMemberships(UserDTO dto);

    /**
    * @description: 根据id修改用户并全量替换角色，不写组织成员
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link User}
    **/
    User editWithMemberships(UserDTO dto);

    /**
    * @description: 根据id逻辑删除用户，并级联清除组织挂靠与角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: boolean
    **/
    boolean deleteUser(Long id);

    /**
    * @description: 根据id查询用户详情，含组织与角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link UserVO}
    **/
    UserVO infoWithMemberships(Long id);

    /**
    * @description: 新增用户（兼容旧接口，不涉及成员写入）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [user]
    * @return: {@link User}
    **/
    User insert(User user);

    /**
    * @description: 根据id逻辑删除用户
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: boolean
    **/
    boolean delete(Long id);

    /**
    * @description: 根据id修改用户基础字段
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [user]
    * @return: boolean
    **/
    boolean edit(User user);

    /**
    * @description: 根据id查询用户
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link User}
    **/
    User selectById(Long id);

    /**
    * @description: 分页查询用户，可按组织过滤成员
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Page}<{@link User}>
    **/
    Page<User> list(UserQueryDTO query);

    /**
    * @description: 根据用户名查询用户
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [username]
    * @return: {@link User}
    **/
    User selectByUsername(String username);

    /**
    * @description: 批量修改用户状态
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ids, status]
    * @return: boolean
    **/
    boolean updateStatus(List<Long> ids, Integer status);

    /**
    * @description: 根据用户id分页查询已授角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Page}<{@link Role}>
    **/
    Page<Role> listRoles(UserQueryDTO query);

    /**
    * @description: 根据用户id分页查询权限，含禁用，按角色汇总去重
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Page}<{@link Permission}>
    **/
    Page<Permission> listPermissions(UserQueryDTO query);
}
