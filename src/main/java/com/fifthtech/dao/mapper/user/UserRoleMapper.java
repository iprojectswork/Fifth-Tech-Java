package com.fifthtech.dao.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.user.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UserRoleMapper
 *
 * <p>用户角色关联 Mapper（C4 §4.3 / §4.8）。
 * 写操作均走 XML（{@code INSERT} / {@code DELETE} 带集合参数）。
 * 角色已带 {@code org_id}，「用户撤掉组织成员」级联删该组织下任职通过
 * {@link #deleteByUserIdAndOrgIds} 实现。</p>
 *
 * @author RH
 * @description 用户角色关联Mapper接口
 * @date 2026-03-22
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID删除用户角色关联
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关联
     */
    void batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 删除指定用户在某些组织下挂靠角色的任职记录（C4 §4.8：
     * 用户保存时若 {@code orgIds} 缩减，级联删除该组织下 {@code user_role}）。
     * 通过 {@code sys_user_org} 不存在或 {@code sys_role.org_id IN (:orgIds)} 判定。
     */
    void deleteByUserIdAndOrgIds(@Param("userId") Long userId, @Param("orgIds") List<Long> orgIds);

    /**
     * 查询用户在某些组织下挂靠的角色 ID 集合（C4 §4.8）。
     */
    List<Long> selectRoleIdsByUserIdAndOrgIds(@Param("userId") Long userId,
                                              @Param("orgIds") List<Long> orgIds);

    /**
     * 按 userId 取当前全部任职角色 ID 集合（{@code DISTINCT}）。
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}