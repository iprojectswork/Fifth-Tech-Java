package com.fifthtech.controller.permission;

import lombok.Data;

import java.util.List;

/**
 * PermissionTreeVO
 *
 * @author RH
 * @description 权限树视图对象
 * @date 2026-03-22
 * @version 1.0
 */
@Data
public class PermissionTreeVO {

    /**
     * 权限ID
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
     * 权限类型（1：菜单，2：按钮）
     */
    private Integer permissionType;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 路由路径（菜单类型）
     */
    private String path;

    /**
     * 组件路径（菜单类型）
     */
    private String component;

    /**
     * 图标（菜单类型）
     */
    private String icon;

    /**
     * 状态（0：禁用，1：启用）
     */
    private Integer status;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 子权限列表
     */
    private List<PermissionTreeVO> children;
}
