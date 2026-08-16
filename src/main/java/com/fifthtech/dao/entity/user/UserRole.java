package com.fifthtech.dao.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author RH
 * @ClassName UserRole
 * @description: 用户角色关联实体
 * @date 2026年08月16日
 * @version: 1.0
 */
@Data
@TableName("sys_user_role")
public class UserRole {

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
     * 角色 ID
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}