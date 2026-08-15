package com.fifthtech.dao.mapper.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.org.UserOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UserOrgMapper
 *
 * <p>用户 ↔ 组织 成员关系 Mapper（C4 §4.2 / §4.8）。
 * 写操作均走 XML（{@code INSERT} / {@code DELETE} 带集合参数）。</p>
 *
 * @author RH
 * @description 用户组织挂靠 Mapper
 * @date 2026-08-09
 */
@Mapper
public interface UserOrgMapper extends BaseMapper<UserOrg> {

    /**
     * 根据用户 ID 删除其全部成员关系（用户删除场景）
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除用户对某组织的成员关系
     */
    void deleteByUserIdAndOrgId(@Param("userId") Long userId, @Param("orgId") Long orgId);

    /**
     * 批量插入成员关系（仅插入未存在的，避免唯一约束冲突）
     */
    void batchInsertIgnore(@Param("userId") Long userId, @Param("orgIds") List<Long> orgIds);

    /**
     * 查询用户当前全部成员组织 ID（用于收敛对比）
     */
    List<Long> selectOrgIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询某组织下的成员用户 ID 列表（用于「users-by-role」等关联查询的前置或辅助场景）
     */
    List<Long> selectUserIdsByOrgId(@Param("orgId") Long orgId);

    /**
     * 查询若干组织下的成员用户 ID（去重）
     */
    List<Long> selectUserIdsByOrgIds(@Param("orgIds") List<Long> orgIds);

    /**
     * 删除用户在指定组织集合上的挂靠
     */
    void deleteByUserIdAndOrgIds(@Param("userId") Long userId, @Param("orgIds") List<Long> orgIds);

    /**
     * 查询某用户的成员组织 ID 是否包含 orgId（{@code exists} 判定走 {@code EXISTS} 子查询）
     */
    boolean existsUserOrg(@Param("userId") Long userId, @Param("orgId") Long orgId);
}