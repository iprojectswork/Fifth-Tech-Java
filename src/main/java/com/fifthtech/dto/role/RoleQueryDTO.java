package com.fifthtech.dto.role;

import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName RoleQueryDTO
 * @description: 角色查询条件
 * @date 2026年08月15日
 * @version: 1.0
 */
@Data
public class RoleQueryDTO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private Integer current;

    /**
     * 每页条数
     */
    private Integer size;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;
}