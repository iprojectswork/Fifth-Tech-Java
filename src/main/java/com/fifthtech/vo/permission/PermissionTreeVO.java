package com.fifthtech.vo.permission;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * @author RH
 * @ClassName PermissionTreeVO
 * @description: 权限树视图
 * @date 2026年03月22日
 * @version: 1.0
 */
@Data
public class PermissionTreeVO {

    /**
     * 权限 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonSerialize(using = ToStringSerializer.class)
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

    /**
     * 是否有未删子权限
     */
    private Boolean hasChildren;

    /**
     * 子权限列表
     */
    private List<PermissionTreeVO> children;
}