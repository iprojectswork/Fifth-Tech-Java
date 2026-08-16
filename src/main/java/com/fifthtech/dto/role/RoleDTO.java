package com.fifthtech.dto.role;

import lombok.Data;

/**
 * @author RH
 * @ClassName RoleDTO
 * @description: 角色数据传输对象
 * @date 2026年03月22日
 * @version: 1.0
 */
@Data
public class RoleDTO {

    /**
     * 主键（edit 时必填）
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码（全局唯一）
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态（1：启用 0：禁用）
     */
    private Integer status;

    /**
     * 排序号
     */
    private Integer sort;
}