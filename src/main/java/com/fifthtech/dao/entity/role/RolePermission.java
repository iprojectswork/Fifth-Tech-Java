package com.fifthtech.dao.entity.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role_permission")
public class RolePermission {

    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;

    @TableField("role_id")
    private Long roleId;

    @TableField("permission_id")
    private Long permissionId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
