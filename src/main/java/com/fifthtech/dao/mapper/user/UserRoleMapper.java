package com.fifthtech.dao.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.user.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UserRoleMapper
 *
 * @author RH
 * @description 用户角色关联Mapper接口
 * @date 2026-03-22
 * @version 1.0
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
}
