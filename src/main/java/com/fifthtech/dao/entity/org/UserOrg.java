package com.fifthtech.dao.entity.org;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author RH
 * @ClassName UserOrg
 * @description: 用户组织挂靠实体
 * @date 2026年08月09日
 * @version: 1.0
 */
@Data
@TableName("sys_user_org")
public class UserOrg {

    /**
     * 主键
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