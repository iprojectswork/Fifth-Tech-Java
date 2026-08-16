package com.fifthtech.dao.entity.permission;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author RH
 * @ClassName Permission
 * @description: 权限实体
 * @date 2026年08月16日
 * @version: 1.0
 */
@Data
@TableName("sys_permission")
public class Permission {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;

    /**
     * 权限名称
     */
    @TableField("permission_name")
    private String permissionName;

    /**
     * 权限编码
     */
    @TableField("permission_code")
    private String permissionCode;

    /**
     * 权限类型（1：菜单 2：按钮）
     */
    @TableField("permission_type")
    private Integer permissionType;

    /**
     * 父权限 ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 路由路径
     */
    @TableField("path")
    private String path;

    /**
     * 组件路径
     */
    @TableField("component")
    private String component;

    /**
     * 图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 状态（1：启用 0：禁用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 排序号
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 创建人 ID
     */
    @TableField("create_id")
    private Long createId;

    /**
     * 创建人姓名
     */
    @TableField("create_name")
    private String createName;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新人 ID
     */
    @TableField("update_id")
    private Long updateId;

    /**
     * 更新人姓名
     */
    @TableField("update_name")
    private String updateName;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除（0：否 1：是）
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 删除人 ID
     */
    @TableField("delete_id")
    private Long deleteId;

    /**
     * 删除人姓名
     */
    @TableField("delete_name")
    private String deleteName;

    /**
     * 删除时间
     */
    @TableField("delete_time")
    private LocalDateTime deleteTime;
}