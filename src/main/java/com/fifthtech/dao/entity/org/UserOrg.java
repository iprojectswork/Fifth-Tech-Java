package com.fifthtech.dao.entity.org;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserOrg
 *
 * <p>用户 ↔ 组织 成员关系（C4 §4.2 / §4.8）。无主属概念，无 is_primary；
 * 唯一约束 {@code (user_id, org_id)} 由 DB 索引 {@code uk_sys_user_org_user_org} 兜底。</p>
 *
 * @author RH
 * @description 用户组织挂靠实体
 * @date 2026-08-09
 */
@Data
@TableName("sys_user_org")
public class UserOrg {

    /**
     * 主键（雪花 ID）
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;

    /**
     * 用户 ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 组织 ID
     */
    @TableField("org_id")
    private Long orgId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}