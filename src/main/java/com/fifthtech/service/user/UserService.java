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
 * UserService
 *
 * <p>用户服务。
 * <ul>
 *   <li>用户表单只写角色；组织成员由组织管理页维护。</li>
 *   <li>{@link #deleteUser(Long)}：级联清 {@code sys_user_org} 与 {@code sys_user_role}。</li>
     *   <li>{@link #list(UserQueryDTO)}：可选 {@code orgId} 精确过滤该组织成员。</li>
 * </ul>
 * </p>
 *
 * @author RH
 * @description 用户服务接口
 * @date 2026-01-25
 */
public interface UserService extends IService<User> {

    /**
     * 新增用户；可选写入角色。不写组织成员。
     */
    User insertWithMemberships(UserDTO dto);

    /**
     * 修改用户；{@code roleIds} 非 null 时全量替换角色。不写组织成员。
     */
    User editWithMemberships(UserDTO dto);

    /**
     * 逻辑删除用户 + 级联清 {@code sys_user_org} 与 {@code sys_user_role}。
     */
    boolean deleteUser(Long id);

    /**
     * 详情（含 orgs / roles 摘要）。
     */
    UserVO infoWithMemberships(Long id);

    User insert(User user);

    boolean delete(Long id);

    boolean edit(User user);

    User selectById(Long id);

    /**
     * 分页查询。可选 {@code query.orgId} 精确过滤成员组织；其它字段作为 username/nickname/email/phone/status 模糊/精确过滤。
     */
    Page<User> list(UserQueryDTO query);

    User selectByUsername(String username);

    boolean updateStatus(List<Long> ids, Integer status);

    Page<Role> listRoles(UserQueryDTO query);

    Page<Permission> listPermissions(UserQueryDTO query);
}