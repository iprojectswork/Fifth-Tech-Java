package com.fifthtech.dao.mapper.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.org.UserOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author RH
 * @ClassName UserOrgMapper
 * @description: 用户组织挂靠Mapper接口
 * @date 2026年08月09日
 * @version: 1.0
 */
@Mapper
public interface UserOrgMapper extends BaseMapper<UserOrg> {

    /**
    * @description: 根据用户id删除全部组织挂靠
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: void
    **/
    void deleteByUserId(@Param("userId") Long userId);

    /**
    * @description: 根据用户id和组织id删除挂靠
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId, orgId]
    * @return: void
    **/
    void deleteByUserIdAndOrgId(@Param("userId") Long userId, @Param("orgId") Long orgId);

    /**
    * @description: 批量插入成员关系，唯一键冲突忽略（PG ON CONFLICT DO NOTHING）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId, orgIds]
    * @return: void
    **/
    void batchInsertIgnore(@Param("userId") Long userId, @Param("orgIds") List<Long> orgIds);

    /**
    * @description: 根据用户id查询挂靠组织id
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> selectOrgIdsByUserId(@Param("userId") Long userId);

    /**
    * @description: 根据组织id查询成员用户id
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [orgId]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> selectUserIdsByOrgId(@Param("orgId") Long orgId);

    /**
    * @description: 根据组织id集合查询成员用户id
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [orgIds]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> selectUserIdsByOrgIds(@Param("orgIds") List<Long> orgIds);

    /**
    * @description: 根据用户id和组织id集合删除挂靠
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId, orgIds]
    * @return: void
    **/
    void deleteByUserIdAndOrgIds(@Param("userId") Long userId, @Param("orgIds") List<Long> orgIds);

    /**
    * @description: 根据用户id和组织id判断是否挂靠
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId, orgId]
    * @return: boolean
    **/
    boolean existsUserOrg(@Param("userId") Long userId, @Param("orgId") Long orgId);
}
