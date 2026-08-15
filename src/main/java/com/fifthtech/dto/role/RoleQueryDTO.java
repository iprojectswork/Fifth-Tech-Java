package com.fifthtech.dto.role;

import lombok.Data;

import java.io.Serializable;

/**
 * RoleQueryDTO
 *
 * @author RH
 * @description 角色查询条件（前端入参）
 * @date 2026-08-15
 */
@Data
public class RoleQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer current;

    private Integer size;

    private String roleName;

    private String roleCode;
}
