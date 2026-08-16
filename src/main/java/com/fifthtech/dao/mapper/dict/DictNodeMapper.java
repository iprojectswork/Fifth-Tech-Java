package com.fifthtech.dao.mapper.dict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.dict.DictNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author RH
 * @ClassName DictNodeMapper
 * @description: 数据字典Mapper接口
 * @date 2026年08月02日
 * @version: 1.0
 */
@Mapper
public interface DictNodeMapper extends BaseMapper<DictNode> {

    /**
    * @description: 查询全量未删字典节点（按 sort/code/id 升序），用于字典树
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link DictNode}>
    **/
    List<DictNode> selectActiveList();

    /**
    * @description: 根据父id查询直接子节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [parentId]
    * @return: {@link List}<{@link DictNode}>
    **/
    List<DictNode> selectChildrenByParentId(@Param("parentId") Long parentId);

    /**
    * @description: 从父ID集合中筛选出「有未删子」的子集，用于树/children 的 hasChildren 标记
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [parentIds]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> selectActiveParentIdsWithChildren(@Param("parentIds") List<Long> parentIds);
}
