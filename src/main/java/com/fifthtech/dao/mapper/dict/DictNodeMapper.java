package com.fifthtech.dao.mapper.dict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.dict.DictNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DictNodeMapper
 *
 * <p>数据字典 Mapper。{@code list} 语义的方法（{@link #selectActiveList} /
 * {@link #selectChildrenByParentId}）走 XML 手写 SQL（D2）；其余 info / count / exists
 * 等可走 MyBatis-Plus（D3）。</p>
 *
 * @author RH
 * @description 数据字典 Mapper
 * @date 2026-08-02
 */
@Mapper
public interface DictNodeMapper extends BaseMapper<DictNode> {

    /**
     * 全量未删节点（用于 {@code /dict/node/tree}），按 sort/code/id 升序
     */
    List<DictNode> selectActiveList();

    /**
     * 指定父节点下的直接子（用于 {@code /dict/node/children} 与 {@code /dict/node/list}），
     * 按 sort/code/id 升序；{@code parentId=0} 取根层第一层
     */
    List<DictNode> selectChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 批量 hasChildren：返回有未删子的 parent_id 集合（用于树/children 标记）
     */
    List<Long> selectActiveParentIdsWithChildren(@Param("parentIds") List<Long> parentIds);
}