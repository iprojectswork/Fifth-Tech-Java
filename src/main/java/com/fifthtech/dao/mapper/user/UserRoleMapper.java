package com.fifthtech.dao.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.user.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author RH
 * @ClassName UserRoleMapper
 * @description: 用户角色关联Mapper接口
 * @date 2026年03月22日
 * @version: 1.0
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
    * @description: 根据用户id删除全部角色任职
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: void
    **/
    void deleteByUserId(@Param("userId") Long userId);

    /**
    * @description: 批量插入角色任职（唯一键冲突忽略）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId, roleIds]
    * @return: void
    **/
    void batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
    * @description: 根据用户id和组织id集合删除角色任职
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId, orgIds]
    * @return: void
    **/
    void deleteByUserIdAndOrgIds(@Param("userId") Long userId, @Param("orgIds") List<Long> orgIds);

    /**
    * @description: 根据用户id和组织id集合查询角色id
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId, orgIds]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> selectRoleIdsByUserIdAndOrgIds(@Param("userId") Long userId,
                                              @Param("orgIds") List<Long> orgIds);

    /**
    * @description: 根据用户id查询任职角色id
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}
