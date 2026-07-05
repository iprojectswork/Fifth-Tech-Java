package com.fifthtech.controller.role;

import lombok.Data;

/**
 * RoleDTO
 *
 * @author RH
 * @description 角色数据传输对象
 * @date 2026-03-22
 * @version 1.0
 */
@Data
public class RoleDTO {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态（0：禁用，1：启用）
     */
    private Integer status;

    /**
     * 排序号
     */
    private Integer sort;
}
