package com.fifthtech.dao.mapper.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.org.Org;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OrgMapper
 *
 * <p>组织 Mapper。{@code list} 语义的查询（{@link #selectActiveList} /
 * {@link #selectChildrenByParentId} / {@link #selectEnabledOptions}）走 XML 手写 SQL（C4 §7.3，
 * 禁 {@code SELECT *}）；其余 info / count / exists 可走 MyBatis-Plus。</p>
 *
 * @author RH
 * @description 组织 Mapper
 * @date 2026-08-09
 */
@Mapper
public interface OrgMapper extends BaseMapper<Org> {

    /**
     * 全量未删组织（用于 {@code /org/tree}），按 sort/code/id 升序
     */
    List<Org> selectActiveList();

    /**
     * 全量启用（{@code status=1}）未删组织（用于 {@code /org/options}），按 sort/code/id 升序
     */
    List<Org> selectEnabledOptions();

    /**
     * 指定父节点下的直接子（用于 {@code /org/children} 与 {@code /org/list}），
     * 按 sort/code/id 升序；{@code parentId=0} 取根层第一层
     */
    List<Org> selectChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 批量 hasChildren：返回有未删子的 parent_id 集合（用于树/children 标记）
     */
    List<Long> selectActiveParentIdsWithChildren(@Param("parentIds") List<Long> parentIds);

    /**
     * 统计 {@code parentId} 下未删子节点数
     */
    long countActiveChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 统计 {@code orgId} 下未删 {@code sys_user_org} 成员数
     */
    long countActiveMembersByOrgId(@Param("orgId") Long orgId);

    /**
     * 统计 {@code orgId} 下未删角色数
     */
    long countActiveRolesByOrgId(@Param("orgId") Long orgId);
}