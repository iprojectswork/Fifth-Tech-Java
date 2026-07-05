package com.fifthtech.dao.entity.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class Role {

    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;

    @TableField("role_name")
    private String roleName;

    @TableField("role_code")
    private String roleCode;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status;

    @TableField("sort")
    private Integer sort;

    @TableField("create_id")
    private Long createId;

    @TableField("create_name")
    private String createName;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_id")
    private Long updateId;

    @TableField("update_name")
    private String updateName;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField("delete_id")
    private Long deleteId;

    @TableField("delete_name")
    private String deleteName;

    @TableField("delete_time")
    private LocalDateTime deleteTime;
}
