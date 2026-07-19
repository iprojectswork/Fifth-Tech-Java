package com.fifthtech.vo.role;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * RoleVO
 *
 * @author RH
 * @description 角色视图对象
 * @date 2026-03-22
 * @version 1.0
 */
@Data
public class RoleVO {

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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
