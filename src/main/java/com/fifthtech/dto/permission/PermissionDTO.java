package com.fifthtech.dto.permission;

import lombok.Data;

/**
 * @author RH
 * @ClassName PermissionDTO
 * @description: 权限数据传输对象
 * @date 2026年03月22日
 * @version: 1.0
 */
@Data
public class PermissionDTO {

    /**
     * 主键（edit 时必填）
     */
    private Long id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限类型（1：菜单 2：按钮）
     */
    private Integer permissionType;

    /**
     * 父权限 ID
     */
    private Long parentId;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态（1：启用 0：禁用）
     */
    private Integer status;

    /**
     * 排序号
     */
    private Integer sort;
}