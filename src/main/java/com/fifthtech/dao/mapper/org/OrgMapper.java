package com.fifthtech.dao.mapper.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.org.Org;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author RH
 * @ClassName OrgMapper
 * @description: 组织Mapper接口
 * @date 2026年08月09日
 * @version: 1.0
 */
@Mapper
public interface OrgMapper extends BaseMapper<Org> {

    /**
    * @description: 查询全量未删组织（按 sort/code/id 升序），用于组织树
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link Org}>
    **/
    List<Org> selectActiveList();

    /**
    * @description: 查询全量启用且未删组织（按 sort/code/id 升序），用于选择器
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link Org}>
    **/
    List<Org> selectEnabledOptions();

    /**
    * @description: 根据父id查询直接子组织
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [parentId]
    * @return: {@link List}<{@link Org}>
    **/
    List<Org> selectChildrenByParentId(@Param("parentId") Long parentId);

    /**
    * @description: 从父ID集合中筛选出「有未删子」的子集，用于树/children 的 hasChildren 标记
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [parentIds]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> selectActiveParentIdsWithChildren(@Param("parentIds") List<Long> parentIds);

    /**
    * @description: 根据父id统计未删子组织数
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [parentId]
    * @return: long
    **/
    long countActiveChildrenByParentId(@Param("parentId") Long parentId);

    /**
    * @description: 根据组织id统计未删成员数
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [orgId]
    * @return: long
    **/
    long countActiveMembersByOrgId(@Param("orgId") Long orgId);

    /**
    * @description: 根据组织id统计未删角色数
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [orgId]
    * @return: long
    **/
    long countActiveRolesByOrgId(@Param("orgId") Long orgId);
}
